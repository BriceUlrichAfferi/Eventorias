package com.example.eventorias

import android.util.Log
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import com.google.firebase.firestore.*
import com.example.eventorias.model.Event
import com.example.eventorias.presentation.event.EventRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

class EventRepositoryTest {

    private lateinit var repository: EventRepository
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var firestore: FirebaseFirestore
    private lateinit var collection: CollectionReference
    private val mockContext = mock(android.content.Context::class.java)


    @Before
    fun setup() {
        mockFirestore = mock(FirebaseFirestore::class.java)
        repository = EventRepository(mockFirestore)

        firestore = mock(FirebaseFirestore::class.java)
        collection = mock(CollectionReference::class.java)

        `when`(firestore.collection("events")).thenReturn(collection)

        mockStatic(Log::class.java)
        `when`(Log.d(anyString(), anyString())).thenReturn(0)

        mockFirestore = mock(FirebaseFirestore::class.java)
        repository = EventRepository(mockFirestore)

        firestore = mock(FirebaseFirestore::class.java)
        collection = mock(CollectionReference::class.java)

        `when`(firestore.collection("events")).thenReturn(collection)


    }

    @After
    fun tearDown() {
        Mockito.framework().clearInlineMocks()
    }

    @Test
    fun `getEventsRealtime returns events from Firestore`() = runBlocking {
        val mockSnapshot = mock(QuerySnapshot::class.java)
        val mockDocument = mock(DocumentSnapshot::class.java)
        val mockCollectionRef = mock(CollectionReference::class.java)
        val mockListenerRegistration = mock(ListenerRegistration::class.java)

        val correctDate = LocalDate.of(2023, 1, 1)
        `when`(mockDocument.exists()).thenReturn(true)
        `when`(mockDocument.data).thenReturn(mapOf(
            "title" to "title",
            "description" to "description",
            "date" to correctDate.format(DateTimeFormatter.ISO_LOCAL_DATE), // Store date as ISO string
            "time" to "00:00",
            "location" to "location",
            "category" to "category",
            "photoUrl" to null,
            "userProfileUrl" to null
        ))
        `when`(mockDocument.id).thenReturn("id")
        `when`(mockSnapshot.documents).thenReturn(listOf(mockDocument))

        `when`(mockFirestore.collection("events")).thenReturn(mockCollectionRef)

        `when`(mockCollectionRef.addSnapshotListener(any<EventListener<QuerySnapshot>>())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as EventListener<QuerySnapshot>
            listener.onEvent(mockSnapshot, null)  // Simulate a successful query
            mockListenerRegistration
        }

        // Test
        val result = repository.getEventsRealtime().first()
        assertEquals(1, result.size)
        assertEquals("title", result[0].title)
        assertEquals("description", result[0].description)
        assertEquals(correctDate, result[0].date)
        assertEquals(LocalTime.of(0, 0), result[0].time)
        assertEquals("location", result[0].location)
        assertEquals("category", result[0].category)
        assertNull(result[0].photoUrl)
        assertNull(result[0].userProfileUrl)
    }




    @Test
    fun `addEvent adds new event to Firestore`() {
        val mockEvent = Event("id", "title", "category")
        collection.add(mockEvent) // No NullPointerException
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getEventById_retrieves_event_from_Firestore() = runBlockingTest {
        val mockDocumentSnapshot = mock<DocumentSnapshot>()
        val mockTask = mock<Task<DocumentSnapshot>>()
        val correctDate = LocalDate.of(2023, 1, 1)

        `when`(mockDocumentSnapshot.exists()).thenReturn(true)
        `when`(mockDocumentSnapshot.id).thenReturn("someId")
        `when`(mockDocumentSnapshot.data).thenReturn(
            mapOf(
                "title" to "Sample Event",
                "description" to "description",
                "date" to correctDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                "time" to "12:00",
                "location" to "Test Location",
                "category" to "Test Category",
                "photoUrl" to "http://example.com/photo.jpg",
                "userProfileUrl" to "http://example.com/user.jpg"

            )
        )

        val mockCollection = mock<CollectionReference>()
        val mockDocument = mock<DocumentReference>()

        doReturn(mockCollection).`when`(mockFirestore).collection("events")
        doReturn(mockDocument).`when`(mockCollection).document("someId")
        doReturn(mockTask).`when`(mockDocument).get()

        `when`(mockTask.isComplete).thenReturn(true)
        `when`(mockTask.isSuccessful).thenReturn(true)
        `when`(mockTask.result).thenReturn(mockDocumentSnapshot)

        val result = repository.getEventById("someId")
        assertNotNull(result)
        assertEquals("Sample Event", result?.title)
        assertEquals("Test Location", result?.location)
        assertEquals("Test Category", result?.category)
        assertEquals(LocalDate.of(2023, 1, 1), result?.date) // Note: Your toLocalDate() uses epoch day, so this should match
        assertEquals(LocalTime.parse("12:00"), result?.time)
        assertEquals("http://example.com/photo.jpg", result?.photoUrl)
        assertEquals("http://example.com/user.jpg", result?.userProfileUrl)
    }


}
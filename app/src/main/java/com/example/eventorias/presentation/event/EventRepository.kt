package com.example.eventorias.presentation.event


import com.example.eventorias.model.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class EventRepository(private val firestore: FirebaseFirestore) {

    /**
     * Retrieves a flow of events ordered by date in descending order.
     */
    fun getEventsRealtime(): Flow<List<Event>> = callbackFlow {
        val listener = firestore.collection("events")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull { document ->
                    Event.fromFirestore(document)
                } ?: emptyList()

                trySend(events)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Adds a new event to Firestore.
     * @param event The Event object to save.
     * @return The ID of the newly created document.
     */
    suspend fun addEvent(event: Event): String {
        val document = firestore.collection("events").add(Event.toFirestore(event)).await()
        return document.id
    }

    /**
     * Retrieves an event by its ID from Firestore.
     */
    suspend fun getEventById(eventId: String): Event? = firestore.collection("events")
        .document(eventId)
        .get()
        .await()
        .let {
            Event.fromFirestore(it)
        }

    /**
     * Updates an existing event in Firestore.
     */
    suspend fun updateEvent(eventId: String, event: Event) {
        firestore.collection("events").document(eventId).set(Event.toFirestore(event)).await()
    }

    /**
     * Deletes an event from Firestore.
     */
    suspend fun deleteEvent(eventId: String) {
        firestore.collection("events").document(eventId).delete().await()
    }
}
package com.example.eventorias.presentation.event

import com.example.eventorias.model.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FieldValue

class EventRepository(private val firestore: FirebaseFirestore) {

    /**
     * Retrieves a flow of events ordered by date in descending order.
     */
    fun getEventsRealtime(): Flow<List<Event>> = callbackFlow {
        val listener = firestore.collection("events")
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
     * Retrieves a flow of events ordered by date in descending order.
     */
    fun getEventsRealtimeSortedByDate(): Flow<List<Event>> = callbackFlow {
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


    fun getEventsRealtimeSortedByTimeCreated(): Flow<List<Event>> = callbackFlow {
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
     * Retrieves a flow of events sorted by category in ascending order.
     */
    fun getEventsRealtimeSortedByCategory(): Flow<List<Event>> = callbackFlow {
        val listener = firestore.collection("events")
            .orderBy("category", Query.Direction.ASCENDING)
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

        awaitClose { listener.remove()
        }
    }



    /**
     * Adds a new event to Firestore.
     * @param event The Event object to save.
     * @return The ID of the newly created document.
     */

    suspend fun addEvent(event: Event): String {
        val document = firestore.collection("events")
            .add(Event.toFirestore(event.copy(createdAt = FieldValue.serverTimestamp())))
            .await()
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


}
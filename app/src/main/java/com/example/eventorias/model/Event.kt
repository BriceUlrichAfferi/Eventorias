package com.example.eventorias.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.time.LocalDate
import java.time.LocalTime

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: LocalDate = LocalDate.now(),  // Default to current date
    val time: LocalTime = LocalTime.now(), // Default to current time
    val location: String = "",
    val category: String = "",
    val photoUrl: String? = null
) {
    companion object {
        fun fromFirestore(document: DocumentSnapshot): Event? {
            val map = document.data
            if (map == null) return null

            val timestamp = map["date"] as? Timestamp
            if (timestamp == null) return null

            val date = LocalDate.ofEpochDay(timestamp.toDate().time / (24 * 60 * 60 * 1000))
            val time = LocalTime.ofNanoOfDay((timestamp.seconds % 86400) * 1_000_000_000L + timestamp.nanoseconds)

            return Event(
                id = document.id,
                title = map["title"] as? String ?: "",
                description = map["description"] as? String ?: "",
                date = date,
                time = time,
                location = map["location"] as? String ?: "",
                category = map["category"] as? String ?: "",
                photoUrl = map["photoUrl"] as? String
            )
        }

        fun toFirestore(event: Event): Map<String, Any> {
            val timestamp = Timestamp(event.date.atTime(event.time).toEpochSecond(java.time.ZoneOffset.UTC), 0)
            return mapOf(
                "id" to event.id,
                "title" to event.title,
                "description" to event.description,
                "date" to timestamp,
                "location" to event.location,
                "category" to event.category,
                "photoUrl" to (event.photoUrl ?: "")
            )
        }

    }
}
package com.example.eventorias.model

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val location: String = "",
    val category: String = "",
    val photoUrl: String? = null,
    val userProfileUrl: String? = null
) {
    companion object {

        // Fetch from Firestore
        fun fromFirestore(document: DocumentSnapshot): Event? {
            val map = document.data ?: return null

            return Event(
                id = document.id,
                title = map["title"] as? String ?: "",
                description = map["description"] as? String ?: "",
                date = (map["date"] as? String)?.let {
                    LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
                } ?: LocalDate.ofEpochDay(0),
                time = (map["time"] as? String)?.let {
                    LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME)
                } ?: LocalTime.of(0, 0),
                location = map["location"] as? String ?: "",
                category = map["category"] as? String ?: "",
                photoUrl = map["photoUrl"] as? String,
                userProfileUrl = map["userProfileUrl"] as? String
            ).also {
                Log.d("Event", "Parsed Event: $it")
            }
        }

        // Store to Firestore
        fun toFirestore(event: Event): Map<String, Any> {
            return mapOf(
                "id" to event.id,
                "title" to event.title,
                "description" to event.description,
                "date" to event.date.format(DateTimeFormatter.ISO_LOCAL_DATE), // Store date as string in ISO format
                "time" to event.time.format(DateTimeFormatter.ISO_LOCAL_TIME), // Store time as string in ISO format
                "location" to event.location,
                "category" to event.category,
                "photoUrl" to (event.photoUrl ?: ""),
                "userProfileUrl" to (event.userProfileUrl ?: "")
            )
        }
    }
}
package com.example.eventorias.model

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.text.SimpleDateFormat
import java.util.Date
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: LocalDate = LocalDate.now(),
    val createdAt: Any = Date(),
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
                date = try {
                    (map["date"] as? String)?.let {
                        LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
                    } ?: LocalDate.now()
                } catch (e: DateTimeParseException) {
                    LocalDate.now()
                },
                time = try {
                    (map["time"] as? String)?.let {
                        LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME)
                    } ?: LocalTime.now()
                } catch (e: DateTimeParseException) {
                    LocalTime.now()
                },
                location = map["location"] as? String ?: "",
                createdAt = when (val createdAt = map["createdAt"]) {
                    is Timestamp -> createdAt.toDate()
                    is Date -> createdAt
                    is String -> {
                        try {
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(createdAt)
                        } catch (e: Exception) {
                            Log.e("Event", "Error parsing createdAt: $createdAt", e)
                            Date()
                        }
                    }
                    else -> {
                        Date()
                    }
                },
                category = map["category"] as? String ?: "",
                photoUrl = map["photoUrl"] as? String,
                userProfileUrl = map["userProfileUrl"] as? String
            ).also {
            }
        }

        // Store to Firestore
        fun toFirestore(event: Event): Map<String, Any> {
            return mapOf(
                "id" to event.id,
                "title" to event.title,
                "description" to event.description,
                "date" to event.date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                "time" to event.time.format(DateTimeFormatter.ISO_LOCAL_TIME),
                "createdAt" to event.createdAt, // This will handle both Date and FieldValue
                "location" to event.location,
                "category" to event.category,
                "photoUrl" to (event.photoUrl ?: ""),
                "userProfileUrl" to (event.userProfileUrl ?: "")
            )
        }
    }
}
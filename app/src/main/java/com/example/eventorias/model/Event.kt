package com.example.eventorias.model

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.time.LocalDate
import java.time.LocalTime

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val location: String = "",
    val category: String = "",
    val photoUrl: String? = null,
    val userProfileUrl: String? = null  // <-- Add this field
)
 {
     companion object {
         fun fromFirestore(document: DocumentSnapshot): Event? {
             val map = document.data ?: return null

             return Event(
                 id = document.id,
                 title = map["title"] as? String ?: "",
                 description = map["description"] as? String ?: "",
                 date = (map["date"] as? String)?.let { LocalDate.parse(it) } ?: LocalDate.now(),
                 time = (map["time"] as? String)?.let { LocalTime.parse(it) } ?: LocalTime.now(),
                 location = map["location"] as? String ?: "",
                 category = map["category"] as? String ?: "",
                 photoUrl = map["photoUrl"] as? String,
                 userProfileUrl = map["userProfileUrl"] as? String // <-- Fetch from Firestore
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
                 "photoUrl" to (event.photoUrl ?: ""),
                 "userProfileUrl" to (event.userProfileUrl ?: "") // <-- Store it back to Firestore
             )
         }
     }

 }
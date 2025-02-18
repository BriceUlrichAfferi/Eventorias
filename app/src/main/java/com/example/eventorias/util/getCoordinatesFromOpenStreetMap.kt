package com.example.eventorias.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL
import java.net.URLEncoder

suspend fun getCoordinatesFromOpenStreetMap(
    location: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO

): Pair<Double, Double>? {
    return try {
        val encodedLocation = URLEncoder.encode(location, "UTF-8")
        val url = "https://nominatim.openstreetmap.org/search?format=json&accept-language=en&q=$encodedLocation"


        // Use the provided dispatcher
        val response = withContext(dispatcher) {
            URL(url).readText()
        }

        val jsonArray = JSONArray(response)
        if (jsonArray.length() > 0) {
            val jsonObject = jsonArray.getJSONObject(0)
            val latitude = jsonObject.getDouble("lat")
            val longitude = jsonObject.getDouble("lon")
            Pair(latitude, longitude)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}



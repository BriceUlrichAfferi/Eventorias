package com.example.eventorias.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL
import java.net.URLEncoder

suspend fun getCoordinatesFromOpenStreetMap(location: String): Pair<Double, Double>? {
    return try {
        val encodedLocation = URLEncoder.encode(location, "UTF-8")
        val url = "https://nominatim.openstreetmap.org/search?format=json&accept-language=en&q=$encodedLocation"

        Log.d("GeocodingDebug", "Requesting: $url")

        val response = withContext(Dispatchers.IO) {
            URL(url).readText()
        }

        Log.d("GeocodingDebug", "Response: $response")

        val jsonArray = JSONArray(response)
        if (jsonArray.length() > 0) {
            val jsonObject = jsonArray.getJSONObject(0)
            val latitude = jsonObject.getDouble("lat")
            val longitude = jsonObject.getDouble("lon")
            Log.d("GeocodingDebug", "Coordinates: $latitude, $longitude")
            Pair(latitude, longitude)
        } else {
            Log.e("GeocodingError", "No results found for $location")
            null
        }
    } catch (e: Exception) {
        Log.e("GeocodingError", "Error fetching coordinates: ${e.message}")
        null
    }
}



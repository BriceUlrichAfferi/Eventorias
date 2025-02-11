package com.example.eventorias.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

import java.io.IOException
import java.net.URL
import java.net.URLEncoder

/*fun getCoordinatesFromLocation(location: String, apiKey: String): Pair<Double, Double>? {
    try {
        // URL-encode the location string
        val encodedLocation = URLEncoder.encode(location, "UTF-8")
        val url = "https://maps.googleapis.com/maps/api/geocode/json?address=$encodedLocation&key=$apiKey"


        val response = URL(url).readText()

        if (response.contains("status\":\"OK")) {
            val lat = 41.9028  // Example latitude for Rome
            val lon = 12.4964  // Example longitude for Rome
            return Pair(lat, lon)
        }
    } catch (e: IOException) {
        Log.e("GeocodingError", "Error during API call: ${e.message}")
    } catch (e: Exception) {
        Log.e("GeocodingError", "Error: ${e.message}")
    }

    return null
}*/

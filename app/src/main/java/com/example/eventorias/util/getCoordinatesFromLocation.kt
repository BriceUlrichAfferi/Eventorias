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

fun getCoordinatesFromLocation(location: String, apiKey: String): Pair<Double, Double>? {
    try {
        // URL-encode the location string
        val encodedLocation = URLEncoder.encode(location, "UTF-8")
        val url = "https://maps.googleapis.com/maps/api/geocode/json?address=$encodedLocation&key=$apiKey"

        // Log the API URL to check if it's correct
        Log.d("GeocodingURL", "API URL: $url")

        // Fetch the response
        val response = URL(url).readText()

        // Log the raw response to check for issues
        Log.d("GeocodingResponse", "Response: $response")

        // Parse the response here
        // You would need to extract latitude and longitude from the response
        // If the response is valid, return the coordinates
        // (Assuming JSON parsing is done correctly here)
        // For simplicity, here's a mock response check:
        if (response.contains("status\":\"OK")) {
            val lat = 41.9028  // Example latitude for Rome
            val lon = 12.4964  // Example longitude for Rome
            return Pair(lat, lon)
        } else {
            Log.e("GeocodingError", "Invalid response for location: $location")
        }
    } catch (e: IOException) {
        Log.e("GeocodingError", "Error during API call: ${e.message}")
    } catch (e: Exception) {
        Log.e("GeocodingError", "Error: ${e.message}")
    }

    return null
}

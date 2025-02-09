package com.example.eventorias.util


fun generateGoogleMapsStaticUrl(latitude: Double, longitude: Double, apiKey: String): String {
    val baseUrl = "https://maps.googleapis.com/maps/api/staticmap?"
    val parameters = "center=$latitude,$longitude&zoom=14&size=400x400&markers=$latitude,$longitude&key=$apiKey"
    return baseUrl + parameters
}

package com.example.eventorias.presentation.event

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventorias.model.Event
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
class EventViewModel : ViewModel(), KoinComponent {

    private val repository: EventRepository by inject()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _loadingState = MutableStateFlow(false)

    init {
        fetchEventsBySortOption("createdAt")
    }

    /** Fetches all events in real-time */
    fun fetchEvents() {
        _loadingState.value = true  // Start loading
        viewModelScope.launch {
            try {
                repository.getEventsRealtime().collect { events ->
                    _events.value = events
                    Log.d("EventViewModel", "Fetched unsorted events: ${events.map { it.id }}")
                }
            } catch (e: Exception) {
                _error.value = "An error occurred,\nplease try again later"
            } finally {
                _loadingState.value = false
            }
        }
    }

    /** Fetches events sorted by a given option */
    /** Fetches events sorted by a given option */
    fun fetchEventsBySortOption(sortOption: String) {
        viewModelScope.launch {
            try {
                when (sortOption) {
                    "date" -> repository.getEventsRealtimeSortedByDate().collect { events ->
                        _events.value = events
                    }
                    "category" -> repository.getEventsRealtimeSortedByCategory().collect { events ->
                        _events.value = events
                    }
                    "createdAt" -> repository.getEventsRealtimeSortedByTimeCreated().collect{ events ->
                        _events.value = events
                    }

                    else -> repository.getEventsRealtime().collect { events ->
                        _events.value = events
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error fetching sorted events: ${e.message}"
            }
        }
    }

    /** Adds a new event to the repository */
    fun addEvent(event: Event) {
        viewModelScope.launch {
            try {
                val eventId = repository.addEvent(event)
                _event.value = _event.value?.copy(id = eventId)
            } catch (e: Exception) {
                _error.value = "Failed to add event: ${e.message}"
            }
        }
    }

    /** Fetches a single event by its ID */
    fun getEventById(eventId: String) {
        viewModelScope.launch {
            try {
                _event.value = null //
                repository.getEventById(eventId)?.let {
                    _event.value = it
                } ?: run {
                    _event.value = null
                    _error.value = "Event not found"
                }
            } catch (e: Exception) {
                _error.value = "Error fetching event: ${e.message}"
            }
        }
    }

}


package com.example.eventorias.presentation.event

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventorias.model.Event
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate
import java.time.LocalTime

class EventViewModel : ViewModel(), KoinComponent {

    private val repository: EventRepository by inject()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _event = MutableStateFlow<Event?>(null) // Nullable to handle loading states
    val event: StateFlow<Event?> = _event.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // New MutableStateFlow for loading state
    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState.asStateFlow()

    init {
        fetchEvents()
    }

    /** Fetches all events in real-time */
    fun fetchEvents() {
        _loadingState.value = true  // Start loading
        viewModelScope.launch {
            try {
                repository.getEventsRealtime().collect { events ->
                    _events.value = events
                }
            } catch (e: Exception) {
                _error.value = "An error occurred,\nplease try again later"
            } finally {
                _loadingState.value = false // Stop loading
            }
        }
    }

    /** Fetches events sorted by a given option */
    fun fetchEventsBySortOption(sortOption: String) {
        _loadingState.value = true  // Start loading
        viewModelScope.launch {
            try {
                when (sortOption) {
                    "date" -> repository.getEventsRealtimeSortedByDate().collect { events ->
                        _events.value = events
                    }
                    "category" -> repository.getEventsRealtimeSortedByCategory().collect { events ->
                        _events.value = events
                    }
                    else -> fetchEvents() // Default to unsorted
                }
            } catch (e: Exception) {
                _error.value = "Error fetching events"
            } finally {
                _loadingState.value = false // Stop loading
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
                _event.value = null // Reset previous state to indicate loading
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

    /** Updates an existing event */
    fun updateEvent(event: Event) {
        viewModelScope.launch {
            try {
                if (_event.value?.id?.isNotEmpty() == true) {
                    repository.updateEvent(_event.value!!.id, event)
                    _event.value = event
                } else {
                    _error.value = "Event ID not set"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update event: ${e.message}"
            }
        }
    }

    /** Deletes the current event */
    fun deleteEvent() {
        viewModelScope.launch {
            try {
                _event.value?.id?.let { eventId ->
                    repository.deleteEvent(eventId)
                    _event.value = null // Reset event after deletion
                } ?: run {
                    _error.value = "Event ID not set"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete event: ${e.message}"
            }
        }
    }

    /** Updates individual fields of an event */
    fun updateEventTitle(title: String) {
        _event.value = _event.value?.copy(title = title)
    }

    fun updateEventDescription(description: String) {
        _event.value = _event.value?.copy(description = description)
    }

    fun updateEventDate(date: LocalDate) {
        _event.value = _event.value?.copy(date = date)
    }

    fun updateEventTime(time: LocalTime) {
        _event.value = _event.value?.copy(time = time)
    }

    /** Sorting methods */
    fun sortEventsByDate() {
        _events.value = _events.value.sortedWith { event1, event2 ->
            event1.date.atTime(event1.time).compareTo(event2.date.atTime(event2.time))
        }
    }

    fun sortEventsByCategory() {
        _events.value = _events.value.sortedBy { it.category }
    }
}
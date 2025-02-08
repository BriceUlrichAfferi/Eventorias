package com.example.eventorias.presentation.event


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventorias.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate
import java.time.LocalTime

class EventViewModel : ViewModel(), KoinComponent {

    private val repository: EventRepository by inject()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _event = MutableStateFlow<Event>(Event())
    val event: StateFlow<Event> = _event.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchEvents()
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            repository.getEventsRealtime().collect { events ->
                _events.value = events
            }
        }
    }

    fun addEvent(event: Event) {
        viewModelScope.launch {
            try {
                val eventId = repository.addEvent(event)
                // Handle success, maybe notify UI or update local state
                _event.value = _event.value.copy(id = eventId)
            } catch (e: Exception) {
                _error.value = "Failed to add event: ${e.message}"
            }
        }
    }

    fun getEventById(eventId: String) {
        viewModelScope.launch {
            try {
                repository.getEventById(eventId)?.let {
                    _event.value = it
                } ?: run {
                    _error.value = "Event not found"
                }
            } catch (e: Exception) {
                _error.value = "Error fetching event: ${e.message}"
            }
        }
    }

    fun updateEvent(event: Event) {
        viewModelScope.launch {
            try {
                if (_event.value.id.isNotEmpty()) {
                    repository.updateEvent(_event.value.id, event)
                    _event.value = event
                } else {
                    _error.value = "Event ID not set"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update event: ${e.message}"
            }
        }
    }

    fun deleteEvent() {
        viewModelScope.launch {
            try {
                if (_event.value.id.isNotEmpty()) {
                    repository.deleteEvent(_event.value.id)
                    _event.value = Event()  // Reset current event state
                } else {
                    _error.value = "Event ID not set"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete event: ${e.message}"
            }
        }
    }

    fun updateEventTitle(title: String) {
        _event.value = _event.value.copy(title = title)
    }

    fun updateEventDescription(description: String) {
        _event.value = _event.value.copy(description = description)
    }

    fun updateEventDate(date: LocalDate) {
        _event.value = _event.value.copy(date = date)
    }

    fun updateEventTime(time: LocalTime) {
        _event.value = _event.value.copy(time = time)
    }

    // Add more methods for updating other fields as needed
}
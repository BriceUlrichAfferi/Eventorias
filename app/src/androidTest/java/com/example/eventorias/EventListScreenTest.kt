package com.example.eventorias


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.eventorias.model.Event
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@RunWith(AndroidJUnit4::class)
class EventListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleEvent = Event(
        id = "1",
        title = "Sample Event",
        description = "This is a test description",
        date = LocalDate.of(2025, 2, 14),
        time = LocalTime.of(10, 30),
        location = "Test Location",
        category = "Test Category",
        photoUrl = null,
        userProfileUrl = null
    )

    @Test
    fun testEventDisplay() {
        // Set up the Compose environment
        composeTestRule.setContent {
            MaterialTheme {
                EventCard(event = sampleEvent)
            }
        }

        composeTestRule.onNodeWithText("Sample Event").assertIsDisplayed()

        composeTestRule.onNodeWithText("This is a test description").assertIsDisplayed()

        composeTestRule.onNodeWithText("2025-02-14").assertIsDisplayed()
        composeTestRule.onNodeWithText("10:30").assertIsDisplayed()

        composeTestRule.onNodeWithText("Test Location").assertIsDisplayed()
    }
}

@Composable
fun EventCard(event: Event) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = event.title, style = MaterialTheme.typography.titleLarge)
        Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
        Text(text = event.date.toString(), style = MaterialTheme.typography.bodyMedium)
        Text(text = event.time.toString(), style = MaterialTheme.typography.bodyMedium)
        Text(text = event.location, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEventCard() {
    val previewEvent = Event(
        id = "1",
        title = "Preview Event",
        description = "This is a preview description",
        date = LocalDate.now(),
        time = LocalTime.now(),
        location = "Preview Location",
        category = "Preview Category",
        photoUrl = null,
        userProfileUrl = null
    )
    EventCard(event = previewEvent)
}
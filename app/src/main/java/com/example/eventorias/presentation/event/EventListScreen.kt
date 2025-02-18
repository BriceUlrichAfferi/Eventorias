@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.eventorias.presentation.event

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.eventorias.R
import com.example.eventorias.model.Event
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.eventorias.presentation.sign_in.Userdata
import kotlinx.coroutines.delay
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    modifier: Modifier = Modifier,
    users: Map<String, Userdata>,
    onFABClick: () -> Unit = {},
    context: Context,
    onEventClick: (Event) -> Unit
) {
    val eventViewModel: EventViewModel = viewModel()
    val events by eventViewModel.events.collectAsState(initial = emptyList())
    val errorMessage by eventViewModel.error.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("date") }
    var isSearchExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { EventListTopAppBar(isSearchExpanded, searchQuery, onSearchQueryChange = { searchQuery = it }, context, onSearchExpandedChange = { isSearchExpanded = it }, eventViewModel, sortOption, onSortOptionChange = { sortOption = it }) },
        modifier = modifier,
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButtonSection(events, errorMessage, onFABClick)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                errorMessage != null -> {
                    ErrorScreen(errorMessage = errorMessage!!, onRetry = { eventViewModel.fetchEvents() })
                }

                events.isEmpty() -> {
                    LoadingScreen()
                }

                else -> {
                    LazyColumn {
                        items(filteredEvents(events, searchQuery)) { event ->
                            Log.d("EventListScreen", "Displaying event: ${event.title}, Date: ${event.date}, CreatedAt: ${event.createdAt}")
                            EventItem(
                                event = event,
                                user = users[event.id] ?: Userdata("", "", "", null, null),
                                onEventClick = onEventClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventListTopAppBar(
    isSearchExpanded: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    context: Context,
    onSearchExpandedChange: (Boolean) -> Unit,
    eventViewModel: EventViewModel,
    sortOption: String,
    onSortOptionChange: (String) -> Unit
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(id = R.string.event_list), color = Color.White)
                SearchField(isSearchExpanded, searchQuery, onSearchQueryChange, context, onSearchExpandedChange)
            }
        },
        actions = {
            SortDropdownMenu(eventViewModel, sortOption, onSortOptionChange)
        }
    )
}

@Composable
fun SearchField(
    isSearchExpanded: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    context: Context,
    onSearchExpandedChange: (Boolean) -> Unit
) {
    if (isSearchExpanded) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search events (ex:'2023-05-01' for date)", color = Color.White) },
            modifier = Modifier
                // Correct usage of weight inside Row
                .padding(start = 16.dp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    Toast.makeText(context, "Searching for $searchQuery", Toast.LENGTH_SHORT).show()
                }
            ),
            trailingIcon = {
                IconButton(onClick = { onSearchExpandedChange(false) }) {
                    Icon(Icons.Default.Close, "Close")
                }
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedContainerColor = colorResource(id = R.color.grey_pro),
                unfocusedContainerColor = colorResource(id = R.color.grey_pro)
            ),
            singleLine = true
        )
    } else {
        IconButton(onClick = { onSearchExpandedChange(true) }) {
            Icon(Icons.Filled.Search, "Expand Search", tint = Color.White)
        }
    }
}

@Composable
fun SortDropdownMenu(eventViewModel: EventViewModel, sortOption: String, onSortOptionChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.SwapVert,
                contentDescription = "Sort",
                tint = Color.White
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Sort by Date") },
                onClick = {
                    onSortOptionChange("date")
                    expanded = false
                    eventViewModel.fetchEventsBySortOption("date")
                }
            )
            DropdownMenuItem(
                text = { Text("Sort by Category") },
                onClick = {
                    onSortOptionChange("category")
                    expanded = false
                    eventViewModel.fetchEventsBySortOption("category")
                }
            )
        }
    }
}

@Composable
fun FloatingActionButtonSection(events: List<Event>, errorMessage: String?, onFABClick: () -> Unit) {
    if (errorMessage == null && events.isNotEmpty()) {
        FloatingActionButton(
            onClick = onFABClick,
            modifier = Modifier,
            containerColor = Color.Red
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.description_button_add),
                tint = Color.White
            )
        }
    }
}

fun filteredEvents(events: List<Event>, searchQuery: String): List<Event> {
    val datePattern = Regex("^\\d{4}-\\d{2}-\\d{2}$")
    return events.filter { event ->
        if (datePattern.matches(searchQuery)) {
            val queryDate = LocalDate.parse(searchQuery)
            event.date == queryDate
        } else {
            event.title.contains(searchQuery, ignoreCase = true) ||
                    event.description.contains(searchQuery, ignoreCase = true) ||
                    event.category.contains(searchQuery, ignoreCase = true)
        }
    }
}



@Composable
fun LoadingScreen() {
    var progress by rememberSaveable { mutableStateOf(0f) }
    val progressAnim = animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 2000, easing = LinearEasing),
        label = "Loading Progress"
    )

    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(200) // Simulate loading steps
            progress += 0.1f
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = progressAnim.value,
            modifier = Modifier
                .size(150.dp)
        )
    }
}


@Composable
fun ErrorScreen(errorMessage: String?, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = Icons.Filled.Error,
            contentDescription = "Error Icon",
            tint = Color.Gray,
            modifier = Modifier.size(64.dp)
        )

        Text(text = stringResource(id = R.string.error), fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = errorMessage ?: stringResource(id = R.string.error_occured), style = MaterialTheme.typography.titleSmall, color = Color.White)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = RectangleShape,
            modifier = Modifier
                .width(159.dp)
                .height(40.dp)
        ) {
            Text(text = "Try Again",
                color = Color.White,
            )
        }
    }
}

@Composable
fun EventItem(event: Event, user: Userdata, onEventClick: (Event) -> Unit) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onEventClick(event) },
        colors = CardDefaults.elevatedCardColors(
            containerColor = colorResource(id = R.color.grey_pro)
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User profile picture from event.userProfileUrl
                if (!event.userProfileUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = event.userProfileUrl,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile Icon",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                }

                // Event details
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = "Category: ${event.category}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Date: ${event.date.format(dateFormatter)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }

            // User linked picture from event.photoUrl
            if (!event.photoUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = event.photoUrl,
                    contentDescription = "Event Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(87.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .align(Alignment.CenterEnd)
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Photo,
                    contentDescription = "Photo Icon",
                    modifier = Modifier
                        .size(75.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                )
            }
        }
    }
}
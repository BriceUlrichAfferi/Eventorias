package com.example.eventorias.presentation.event

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.material3.FloatingActionButton
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.eventorias.R
import com.example.eventorias.model.Event
import com.example.eventorias.presentation.event.EventViewModel
import java.time.format.DateTimeFormatter
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavHostController
import com.example.eventorias.presentation.sign_in.Userdata

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventListScreen(
    modifier: Modifier = Modifier,
    users: Map<String, Userdata>,
    navHostController: NavHostController,
    onFABClick: () -> Unit = {},
    context: Context,
    onEventClick: (Event) -> Unit = { event ->
        try {
            navHostController.navigate("event_details/${event.id}")
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to navigate: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
) {
    val eventViewModel: EventViewModel = viewModel()
    val events by eventViewModel.events.collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var sortOption by remember { mutableStateOf("date") }
    var isSearchExpanded by remember { mutableStateOf(false) }

    // Search logic
    LaunchedEffect(searchQuery) {
        if (searchQuery.isEmpty()) {
            eventViewModel.fetchEvents() // Fetch all events when search is empty
        } else {
            // Here you might want to implement a search filter or re-fetch events if search changes the sort order
            eventViewModel.fetchEvents() // Keep fetching all events, but you could filter them here
        }
    }

    // Sort logic now handled by clicking sort buttons
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(id = R.string.user_profile),
                            color = Color.White
                        )

                        if (isSearchExpanded) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search events") },
                                modifier = Modifier.weight(1f).padding(start = 16.dp),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        Toast.makeText(context, "Searching for $searchQuery", Toast.LENGTH_SHORT).show()
                                    }
                                ),
                                trailingIcon = {
                                    IconButton(onClick = { isSearchExpanded = false }) {
                                        Icon(Icons.Default.Close, "Close")
                                    }
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    cursorColor = MaterialTheme.colorScheme.primary
                                ),
                                singleLine = true
                            )
                        } else {
                            IconButton(onClick = { isSearchExpanded = true }) {
                                Icon(Icons.Filled.Search, "Expand Search", tint = Color.White)
                            }
                        }
                    }
                },
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.Sort, contentDescription = "Sort")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sort by Date") },
                                onClick = {
                                    sortOption = "date"
                                    expanded = false
                                    eventViewModel.fetchEventsBySortOption(sortOption)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort by Category") },
                                onClick = {
                                    sortOption = "category"
                                    expanded = false
                                    eventViewModel.fetchEventsBySortOption(sortOption)
                                }
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier,
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
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
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            items(events.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }) { event ->
                EventItem(event = event, user = users[event.id] ?: Userdata("", "", "", null), onEventClick = onEventClick)
            }
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
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User profile picture from event.userProfileUrl
            if (!event.userProfileUrl.isNullOrEmpty()) {
                Image(
                    painter = rememberImagePainter(
                        data = event.userProfileUrl,
                        builder = { crossfade(true) }
                    ),
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
                Text(text = "Category: ${event.category}", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                Text(text = "Date: ${event.date.format(dateFormatter)}", style = MaterialTheme.typography.bodySmall, color = Color.White)
            }
        }
    }
}

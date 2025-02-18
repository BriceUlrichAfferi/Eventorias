@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.eventorias.presentation.event

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.eventorias.R
import com.example.eventorias.ui.theme.EventoriasTheme
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.eventorias.model.Event
import com.example.eventorias.util.getCoordinatesFromOpenStreetMap
import org.threeten.bp.format.DateTimeFormatter
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    eventId: String,
    eventCategory: String,
    onBackClick: () -> Unit,
    viewModel: EventViewModel = koinViewModel()
) {
    LaunchedEffect(eventId) {
        viewModel.getEventById(eventId)
    }

    val eventState = viewModel.event.collectAsStateWithLifecycle()
    val errorState = viewModel.error.collectAsStateWithLifecycle()

    EventoriasTheme {
        Scaffold(
            topBar = {
                TopBar(eventCategory, onBackClick)
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                when {
                    eventState.value != null -> EventDetailsContent(event = eventState.value!!)
                    errorState.value != null -> ErrorText(error = errorState.value!!)
                    else -> CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun TopBar(eventCategory: String, onBackClick: () -> Unit) {
    TopAppBar(
        title = { Text(eventCategory, color = Color.White) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.contentDescription_go_back),
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun ErrorText(error: String) {
    Text(error, color = Color.Red)
}

@Composable
fun EventDetailsContent(
    event: Event,
    modifier: Modifier = Modifier
) {
    val latitude = rememberSaveable { mutableStateOf(0.0) }
    val longitude = rememberSaveable { mutableStateOf(0.0) }

    LaunchedEffect(event.location) {
        val coordinates = getCoordinatesFromOpenStreetMap(event.location)
        coordinates?.let {
            latitude.value = it.first
            longitude.value = it.second
        }
    }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        EventImage(event)
        EventInfo(event)
        EventDescription(event)
        EventLocation(event, latitude, longitude)
    }
}

@Composable
fun EventImage(event: Event) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (!event.photoUrl.isNullOrEmpty()) {
            AsyncImage(
                model = event.photoUrl,
                contentDescription = "Event Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(364.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Photo,
                contentDescription = "No Image Available",
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun EventInfo(event: Event) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween // Ensures profile is aligned to the right
    ) {
        Column(
            modifier = Modifier.weight(1f) // Allow this column to take up the available space
        ) {
            EventDate(event)
            EventTime(event)
        }
        EventProfile(event) // This will align it to the right
    }
}



@Composable
fun EventDateTime(event: Event) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            //.weight(1f)
    ) {
        EventDate(event)
        EventTime(event)
    }
}

@Composable
fun EventDate(event: Event) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Filled.Event, contentDescription = "Calendar", tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = event.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")), color = Color.White)
    }
}

@Composable
fun EventTime(event: Event) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Filled.AccessTime, contentDescription = "Time", tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = event.time.toString(), color = Color.White)
    }
}

@Composable
fun EventProfile(event: Event) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .padding(start = 8.dp)
            .fillMaxHeight() // Ensure the Box has the proper height for vertical alignment
    ) {
        if (!event.userProfileUrl.isNullOrEmpty()) {
            AsyncImage(
                model = event.userProfileUrl,
                contentDescription = "Event User Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .align(Alignment.Center) // This will align it both horizontally and vertically in the Box
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = stringResource(id = R.string.no_profilePic),
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .align(Alignment.Center) // Same as above, aligning the icon
            )
        }
    }
}


@Composable
fun EventDescription(event: Event) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = event.description ?: stringResource(id = R.string.no_description),
            color = Color.White,
            textAlign = TextAlign.Justify
        )
    }
}

@Composable
fun EventLocation(event: Event, latitude: MutableState<Double>, longitude: MutableState<Double>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = event.location,
            color = Color.White,
            maxLines = 3,
            modifier = Modifier
                .width(167.dp)
                .height(72.dp)
        )

        if (latitude.value != 0.0 && longitude.value != 0.0) {
            val mapUrl = "https://static-maps.yandex.ru/1.x/?ll=${longitude.value},${latitude.value}&z=14&size=300,200&l=map&pt=${longitude.value},${latitude.value},pm2rdl&lang=en_US"
            AsyncImage(
                model = mapUrl,
                contentDescription = "Event Location Map",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(149.dp, 72.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Text(text = "Loading map...", color = Color.White)
        }
    }
}

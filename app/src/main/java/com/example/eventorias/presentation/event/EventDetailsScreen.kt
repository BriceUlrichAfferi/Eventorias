package com.example.eventorias.presentation.event

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.eventorias.R
import com.example.eventorias.model.Event
import com.example.eventorias.ui.theme.EventoriasTheme
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.eventorias.util.getCoordinatesFromOpenStreetMap
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    eventId: String,
    eventCategory: String,
    onBackClick: () -> Unit,
    viewModel: EventViewModel = koinViewModel()
) {
    LaunchedEffect(eventId) {
        viewModel.getEventById(eventId) // Fetch event details when the screen loads
    }

    val eventState = viewModel.event.collectAsStateWithLifecycle()
    val event = eventState.value

    val errorState = viewModel.error.collectAsStateWithLifecycle()
    val error = errorState.value

    EventoriasTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(eventCategory, color = Color.White) },                    navigationIcon = {
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
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    event != null -> EventDetailsContent(event = event,  onBackClick, modifier = Modifier.fillMaxSize())
                    error != null -> Text(error, color = Color.Red)
                    else -> CircularProgressIndicator()
                }
            }
        }
    }
}




@Composable
fun EventDetailsContent(
    event: Event,
    onBackClick: () -> Unit,
    modifier: Modifier
) {
    val latitude = remember { mutableStateOf(0.0) }
    val longitude = remember { mutableStateOf(0.0) }

    LaunchedEffect(event.location) {
        val coordinates = getCoordinatesFromOpenStreetMap(event.location)
        if (coordinates != null) {
            latitude.value = coordinates.first
            longitude.value = coordinates.second
        }
    }

    Column(modifier = modifier) {
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Event,
                            contentDescription = "Calendar",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = event.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = "Time",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = event.time?.toString() ?: "No Time Available",
                            color = Color.White
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    if (!event.userProfileUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = event.userProfileUrl,
                            contentDescription = "Event User Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = stringResource(id = R.string.no_profilePic),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            if (!event.description.isNullOrEmpty()) {
                Text(
                    text = event.description,
                    color = Color.White,
                    textAlign = TextAlign.Justify,
                )
            } else {
                Text(
                    text = stringResource(id = R.string.no_description),
                    color = Color.White
                )
            }
        }

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
                val mapUrl =
                    "https://static-maps.yandex.ru/1.x/?ll=${longitude.value},${latitude.value}&z=14&size=300,200&l=map&pt=${longitude.value},${latitude.value},pm2rdl&lang=en_US"

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
}

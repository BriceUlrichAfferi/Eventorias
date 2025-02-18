package com.example.eventorias.presentation.event

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.eventorias.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Date
import java.util.UUID
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var saveError by remember { mutableStateOf<String?>(null) }

    // File picker launcher
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    // Camera launcher
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pickImageLauncher.launch("image/*") // Redirect to gallery to pick an image
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.create_event_label),
                        color = Color.White
                    )
                },
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
    ) { contentPadding ->
        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title", style = MaterialTheme.typography.titleSmall) },
                    placeholder = { Text("Enter event title", style = MaterialTheme.typography.titleMedium) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = colorResource(id = R.color.grey_pro),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedLabelColor = Color.Red
                    )
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description", style = MaterialTheme.typography.titleSmall) },
                    placeholder = { Text("Enter event description", style = MaterialTheme.typography.titleMedium) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = colorResource(id = R.color.grey_pro),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedLabelColor = Color.Red
                    )
                )

                // Category
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category", style = MaterialTheme.typography.titleSmall) },
                    placeholder = { Text("Enter event category", style = MaterialTheme.typography.titleMedium) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = colorResource(id = R.color.grey_pro),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedLabelColor = Color.Red
                    )
                )

                // Row for Date & Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Black
                        ) },
                        placeholder = { Text("YYYY-MM-DD", style = MaterialTheme.typography.titleMedium) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                            .clickable {
                                // Show DatePickerDialog
                                val calendar = Calendar.getInstance()
                                val year = calendar[Calendar.YEAR]
                                val month = calendar[Calendar.MONTH]
                                val day = calendar[Calendar.DAY_OF_MONTH]

                                DatePickerDialog(
                                    context,
                                    { _, selectedYear, selectedMonth, selectedDay ->
                                        // Update the date with the selected value
                                        date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                                    },
                                    year,
                                    month,
                                    day
                                ).show()
                            },
                        enabled = false, // Prevent manual editing
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = colorResource(id = R.color.grey_pro),
                            focusedContainerColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            focusedLabelColor = Color.Red
                        )
                    )

                    OutlinedTextField(
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Time", style = MaterialTheme.typography.titleSmall) },
                        placeholder = { Text("HH:MM", style = MaterialTheme.typography.titleMedium) },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                            .clickable{
                                // Show TimePickerDialog
                                val calendar = Calendar.getInstance()
                                val hour = calendar[Calendar.HOUR_OF_DAY]
                                val minute = calendar[Calendar.MINUTE]

                                TimePickerDialog(
                                    context,
                                    { _, selectedHour, selectedMinute ->
                                        time = String.format("%02d:%02d", selectedHour, selectedMinute)
                                    },
                                    hour,
                                    minute,
                                    true // Use 24-hour format
                                ).show()
                            },
                        enabled = false, // Prevent manual editing
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = colorResource(id = R.color.grey_pro),
                            focusedContainerColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            focusedLabelColor = Color.Red
                        )
                    )
                }

                // Location
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location", style = MaterialTheme.typography.titleSmall) },
                    placeholder = { Text("Enter full address", style = MaterialTheme.typography.titleMedium) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = colorResource(id = R.color.grey_pro),
                        focusedContainerColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedLabelColor = Color.Red
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Camera Button
                    Button(
                        onClick = {
                            val photoUri = createImageUri(context)
                            imageUri = photoUri
                            takePictureLauncher.launch(photoUri)
                        },
                        modifier = Modifier.size(70.dp),
                        shape = RoundedCornerShape(21.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "Camera",
                            tint = Color.Black
                        )
                    }

                    // Attach File Button
                    Button(
                        onClick = {
                            pickImageLauncher.launch("image/*")
                        },
                        modifier = Modifier.size(70.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AttachFile,
                            contentDescription = "Attach File",
                            tint = Color.White
                        )
                    }
                }

                // Display the selected or captured image
                // Display the selected or captured image with remove option
                imageUri?.let { uri ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(color = Color.Gray.copy(alpha = 0.2f))
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Cancel button to remove the selected image
                        IconButton(
                            onClick = {
                                imageUri = null // Clear the selected image
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = Color.White
                            )
                        }
                    }
                }

            }

            Button(
                onClick = {
                    val currentUser = auth.currentUser
                    if (currentUser == null) {
                        Toast.makeText(context, "You must be logged in to save the event.", Toast.LENGTH_SHORT).show()
                    } else {
                        saveEventToFirestore(
                            title = title,
                            description = description,
                            category = category,
                            date = date,
                            time = time,
                            location = location,
                            imageUri = imageUri,
                            userProfileUrl = currentUser.photoUrl?.toString(),
                            onComplete = { success, errorMessage ->
                                if (success) {
                                    saveError = null
                                    onSaveClick()
                                } else {
                                    saveError = errorMessage ?: "Unknown error occurred"
                                }
                            }
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp, bottom = 16.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RectangleShape,
            ) {
                Text(
                    stringResource(id = R.string.action_valider),
                    style = TextStyle(color = Color.White),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            // Display error if any
            saveError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                )
            }
        }
    }
}

fun createImageUri(context: Context): Uri {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "event_image.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
}

fun saveEventToFirestore(
    title: String,
    description: String,
    category: String,
    date: String,
    createdAt: Any = Date(),
    time: String,
    location: String,
    imageUri: Uri?,
    userProfileUrl: String?,
    onComplete: (Boolean, String?) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    // Create a unique ID for the event
    val eventId = UUID.randomUUID().toString()

    val eventData = mutableMapOf(
        "id" to eventId,
        "title" to title,
        "description" to description,
        "category" to category,
        "date" to date,
        "createdAt" to createdAt,
        "time" to time,
        "location" to location
    )

    if (imageUri != null) {
        // Upload the image to Firebase Storage
        val storageRef = storage.reference.child("events/$eventId.jpg")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {  _ ->  // Replaced taskSnapshot with "_"
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    eventData["photoUrl"] = downloadUri.toString()
                    eventData["userProfileUrl"] = userProfileUrl ?: ""

                    firestore.collection("events").document(eventId)
                        .set(eventData)
                        .addOnSuccessListener {
                            onComplete(true, null)
                        }
                        .addOnFailureListener { e ->
                            onComplete(false, e.message)
                        }
                }
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    } else {
        eventData["userProfileUrl"] = userProfileUrl ?: ""
        firestore.collection("events").document(eventId)
            .set(eventData)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }
}
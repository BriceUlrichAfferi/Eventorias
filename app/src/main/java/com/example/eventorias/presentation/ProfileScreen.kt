package com.example.eventorias.presentation

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.eventorias.R
import com.google.accompanist.permissions.rememberPermissionState
import com.example.eventorias.presentation.notification.NotificationViewModel
import com.example.eventorias.presentation.sign_in.Userdata
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onSignOut: () -> Unit,
    userdata: Userdata?,
    viewModel: NotificationViewModel = koinViewModel()
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val context = LocalContext.current

    val userData = remember { mutableStateOf<Userdata?>(null) }

    LaunchedEffect(userdata?.userId) {
        userdata?.userId?.let { uid ->
            val firestore = FirebaseFirestore.getInstance()
            val userRef = firestore.collection("users").document(uid)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val firstName = document.getString("firstName") ?: "No Name"
                        val lastName = document.getString("lastName") ?: "No Surname"
                        val profilePictureUrl = document.getString("profilePictureUrl") ?: ""
                        val photoUrl = document.getString("photoUrl") ?: ""

                        val email = FirebaseAuth.getInstance().currentUser?.email ?: "No Email"

                        userData.value = Userdata(
                            userId = uid,
                            userName = "$firstName $lastName",
                            email = email,
                            profilePictureUrl = profilePictureUrl,
                            photoUrl = photoUrl
                        )
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    if (userData.value == null) {
        Text("Loading user data...")
        return
    }

    val notificationsEnabled = remember { mutableStateOf(viewModel.areNotificationsEnabled()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile", color = Color.White) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Name Box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = "Name", fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = userData.value?.userName ?: "No Name", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Email Box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(Color.Gray, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = "E-mail", fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = userData.value?.email ?: "No Email", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notification Toggle
            NotificationParameter(
                notificationsEnabled = notificationsEnabled.value,
                onNotificationDisabledClicked = {
                    viewModel.disableNotifications()
                    notificationsEnabled.value = viewModel.areNotificationsEnabled()
                },
                onNotificationEnabledClicked = {
                    viewModel.enableNotifications()
                    notificationsEnabled.value = viewModel.areNotificationsEnabled()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Out Button
            Button(
                onClick = onSignOut,
                modifier = Modifier
                    .padding(16.dp)
                    .width(150.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RectangleShape,
            ) {
                Text(text = "Sign out")
            }
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun NotificationParameter(
    modifier: Modifier = Modifier,
    notificationsEnabled: Boolean,
    onNotificationEnabledClicked: () -> Unit,
    onNotificationDisabledClicked: () -> Unit
) {
    val notificationsPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(50.dp, 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Switch(
            checked = notificationsEnabled,
            onCheckedChange = { isChecked ->
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationsPermissionState?.launchPermissionRequest()
                    }
                    onNotificationEnabledClicked()
                } else {
                    onNotificationDisabledClicked()
                }
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color.Red,
                uncheckedThumbColor = Color.Black,
                uncheckedTrackColor = Color.LightGray
            )
        )

        Text(
            modifier = Modifier
                .padding(8.dp),
            text = stringResource(id = R.string.notifications),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
    }
}

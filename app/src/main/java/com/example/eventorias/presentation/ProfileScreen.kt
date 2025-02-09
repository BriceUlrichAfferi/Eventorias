package com.example.eventorias.presentation

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.eventorias.R
import com.google.accompanist.permissions.rememberPermissionState
import com.example.eventorias.presentation.notification.NotificationViewModel
import com.example.eventorias.presentation.sign_in.Userdata
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userdata: Userdata?,
    onSignOut: () -> Unit,
    viewModel: NotificationViewModel = koinViewModel()
) {
    var notificationsEnabled by remember { mutableStateOf(viewModel.areNotificationsEnabled()) }

    if (userdata == null) {
        Text("Loading user data...")
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.user_profile),
                        color = Color.White
                    )
                },
                actions = {
                    if (userdata.profilePictureUrl != null) {
                        AsyncImage(
                            model = userdata.profilePictureUrl,
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
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
                    .background(
                        colorResource(id = R.color.grey_pro),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = "Name", fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = userdata.userName ?: "No Name", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Email Box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(
                        colorResource(id = R.color.grey_pro),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(text = "E-mail", fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = userdata.email ?: "No Email", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Notification Toggle
            NotificationParameter(
                notificationsEnabled = notificationsEnabled,
                onNotificationDisabledClicked = {
                    viewModel.disableNotifications()
                    notificationsEnabled = viewModel.areNotificationsEnabled()
                },
                onNotificationEnabledClicked = {
                    viewModel.enableNotifications()
                    notificationsEnabled = viewModel.areNotificationsEnabled()
                }
            )

            Spacer(modifier = Modifier.height(50.dp))

            // Sign Out Button
            Button(onClick = onSignOut) {
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

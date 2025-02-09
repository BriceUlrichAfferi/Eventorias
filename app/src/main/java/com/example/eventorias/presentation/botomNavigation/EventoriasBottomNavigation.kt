package com.example.eventorias.presentation.botomNavigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.eventorias.R

@Composable
fun EventoriasBottomNavigation(
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = Color.Black,
        modifier = modifier
    ) {
        Spacer(Modifier.weight(1f)) // Pushes items towards the center

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Event,
                    contentDescription = "calendar icon",
                    tint = Color.White
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.bottom_navigation_events),
                    color = Color.White
                )
            },
            selected = true,
            onClick = {}
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Person Icon",
                    tint = Color.White
                )
            },
            label = {
                Text(
                    text = stringResource(R.string.bottom_navigation_profile),
                    color = Color.White
                )
            },
            selected = false,
            onClick = {}
        )

        Spacer(Modifier.weight(1f)) // Pushes items towards the center
    }
}

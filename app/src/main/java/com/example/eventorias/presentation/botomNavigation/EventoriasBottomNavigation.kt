package com.example.eventorias.presentation.botomNavigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.example.eventorias.R

@Composable
fun EventoriasBottomNavigation(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    onEventClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    NavigationBar(
        containerColor = Color.Black,
        modifier = modifier
    ) {

        // Pushes items towards the center
        Spacer(Modifier.weight(1f))

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
            selected = currentRoute == "event_list",
            onClick = onEventClick,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Gray,
                selectedTextColor = Color.White,
                indicatorColor = colorResource(id = R.color.grey_pro),
                unselectedIconColor = Color.White,
                unselectedTextColor = Color.White
            )
        )

        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Person Icon", tint = Color.White) },
            label = { Text(text = stringResource(R.string.bottom_navigation_profile), color = Color.White) },
            selected = currentRoute == "profile",
            onClick = {
                onProfileClick()
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorResource(id = R.color.grey_pro),
                selectedTextColor = Color.White,
                indicatorColor = colorResource(id = R.color.grey_pro),
                unselectedIconColor = Color.White,
                unselectedTextColor = Color.White
            )
        )


        Spacer(Modifier.weight(1f))
    }
}
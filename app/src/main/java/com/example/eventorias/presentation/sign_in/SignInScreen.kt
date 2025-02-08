package com.example.eventorias.presentation.sign_in

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.eventorias.R
import com.google.android.gms.common.SignInButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale

@Composable
fun SignInScreen(
    state: SignInState,
    onGoogleSignInClick: () -> Unit,
    onEmailSignInClick: () -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.eventorias777ff),
            contentDescription = "Logo of Eventorias",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 25f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(bottom = 16.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                GoogleSignInButton { onGoogleSignInClick() }
            }

            Box(
                modifier = Modifier
                    .wrapContentSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                EmailSignInButton { onEmailSignInClick() }
            }
        }
    }
}


@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    AndroidView(
        factory = { context: Context ->
            SignInButton(context).apply {
                setSize(SignInButton.SIZE_WIDE) // Set button size
                setOnClickListener { onClick() }
            }
        },
        modifier = Modifier
            .height(60.dp)
            .width(240.dp)
            .padding(horizontal = 16.dp)
    )
}



@Composable
fun EmailSignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(200.dp)
            .height(50.dp),
        shape = RectangleShape, // Ensure square edges
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFCC1305),
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(0.dp) // Remove internal padding
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Email Icon at the extreme left
            Icon(
                imageVector = Icons.Filled.Email,
                contentDescription = "Email Icon",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            // Small gap between icon and text
            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign in with Email",
                    color = Color.White
                )
            }
        }
    }
}

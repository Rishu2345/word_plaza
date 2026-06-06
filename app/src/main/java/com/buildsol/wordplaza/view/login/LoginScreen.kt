package com.buildsol.wordplaza.view.login

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buildsol.wordplaza.ui.theme.AppTheme
import com.buildsol.wordplaza.viewModel.homeScreenViewModel.HomeScreenViewModel
import com.buildsol.wordplaza.viewModel.loginViewModel.LoginViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext

private val LoginInk = Color(0xFF1F2430)
private val LoginMuted = Color(0xFF687083)
private val LoginBlue = Color(0xFF4F8CFF)
private val LoginSun = Color(0xFFFFC857)

@Composable
fun LoginScreen(
    viewModel: LoginViewModel
){
    val uiState = viewModel.uiState.collectAsState()
    LoginScreen(
        isLoading = uiState.value.isLoading,
        errorMessage = uiState.value.errorMessage,
        onSignInWithGoogle = viewModel::onSignInClicked
    )
}

@Composable
fun LoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onSignInWithGoogle: (Activity?) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFFBF0),
                        Color(0xFFEFF6FF),
                        Color(0xFFFFFFFF)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White.copy(alpha = 0.92f),
            shadowElevation = 10.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.linearGradient(listOf(LoginBlue, LoginSun))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "W",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 34.sp
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Welcome to WordPlaza",
                        color = LoginInk,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Sign in once, then keep your profile and words synced with Firestore.",
                        color = LoginMuted,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }

                GoogleSignInButton(
                    isLoading = isLoading,
                    onClick = onSignInWithGoogle
                )

                if (!errorMessage.isNullOrBlank()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun GoogleSignInButton(
    isLoading: Boolean,
    onClick: (Activity?) -> Unit
) {
    val activity = (LocalActivity.current)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE1E6F0), RoundedCornerShape(18.dp))
            .clickable(enabled = !isLoading){onClick(activity)}
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                strokeWidth = 2.5.dp
            )
        } else {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF4285F4), Color(0xFF34A853), Color(0xFFFBBC05)))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "G",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
            }
            Spacer(Modifier.size(12.dp))
            Text(
                text = "Sign in with Google",
                color = LoginInk,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun LoginScreenPreview() {
    AppTheme {
        LoginScreen(
            isLoading = false,
            errorMessage = null,
            onSignInWithGoogle = {}
        )
    }
}

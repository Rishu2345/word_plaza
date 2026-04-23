package com.buildsol.wordplaza.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.notable.app.AppUiState
import org.example.notable.images.Image
import org.example.notable.images.imagePainter


@Composable
fun AppNavigationBar(
    appUiState: AppUiState,
    modifier: Modifier = Modifier
) {
    if(appUiState.hideBottomNavigation) return


    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (!appUiState.hidePreviousButton) {
                Button(onClick = appUiState.onPreviousButtonClick,
                    enabled = appUiState.previousButtonEnable,
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )) {

                    Icon(
                        painter = imagePainter(Image.DARK_MODE_MOON),
                        contentDescription = null
                    )

                    Spacer(Modifier.width(8.dp))
                    Text("Previous", fontSize = 20.sp)
                }
            }
            Spacer(modifier.weight(1f))

            if (!appUiState.hideNextButton) {
                Button(
                    onClick = appUiState.onNextButtonClick,
                    enabled = appUiState.nextButtonEnable,
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(" Next ", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))

                    Icon(
                        painter = imagePainter(Image.ADD),
                        contentDescription = null
                    )

                }
            }
        }
    }
}



package com.buildsol.wordplaza.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.notable.app.AppUiState

@Composable
fun HPTopBar(
    appUiState: AppUiState
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .statusBarsPadding()
            .padding(10.dp)
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = appUiState.title,
            modifier = Modifier,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Bold

        )

        if(appUiState.showProgress){
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "${appUiState.step}/${appUiState.totalSteps}",
                modifier = Modifier,
                style = MaterialTheme.typography.bodySmall
            )
            CircularProgressIndicator(
                progress = appUiState.progress,
                modifier = Modifier,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                trackColor = MaterialTheme.colorScheme.onSecondary


            )
        }
            }
    }
}


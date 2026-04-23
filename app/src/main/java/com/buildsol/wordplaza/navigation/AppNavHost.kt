package com.buildsol.wordplaza.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier,
) {
    val respectNavController = remember {
        NotableComposeNavController(navController)
    }

    NavHost(
        navController = navController,
        startDestination = NoteListRoute,
        modifier = modifier,
    ) {

    }
}
package com.buildsol.wordplaza.model

import com.buildsol.wordplaza.navigation.GTCAppRoute
import com.buildsol.wordplaza.navigation.HomeScreenRoute

data class AppUiState(
    val hideBottomNavigation: Boolean = false,
    val hideTopBar:Boolean = false,
    val onNotificationClick: () -> Unit = {},
    val onProfileClick: () -> Unit = {},
    val userName:String? = null,
    val scrollOffSet: Float = 0f,
    val currentRoute: GTCAppRoute = HomeScreenRoute,
    val onNavigate: (GTCAppRoute) -> Unit = {}
)

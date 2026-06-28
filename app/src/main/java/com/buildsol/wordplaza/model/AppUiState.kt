package com.buildsol.wordplaza.model

import com.buildsol.wordplaza.navigation.GTCAppRoute
import com.buildsol.wordplaza.navigation.HomeScreenRoute

data class AppUiState(
    var hideBottomNavigation: Boolean = false,
    var hideTopBar:Boolean = false,
    var onNotificationClick: () -> Unit = {},
    var onProfileClick: () -> Unit = {},
    var userName:String? = null,
    var scrollOffSet: Float = 0f,
    var currentRoute: GTCAppRoute = HomeScreenRoute,
    var onNavigate: (GTCAppRoute) -> Unit = {},
    var isLoading : Boolean = false,
    var showPostBottomSheet:Boolean = false
)
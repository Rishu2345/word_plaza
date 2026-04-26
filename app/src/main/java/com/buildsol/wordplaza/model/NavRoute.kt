package com.buildsol.wordplaza.model

import androidx.compose.ui.graphics.vector.ImageVector
import com.buildsol.wordplaza.navigation.GTCAppRoute


data class NavItem(
    val route: GTCAppRoute,
    val label: String,
    val icon: ImageVector
)
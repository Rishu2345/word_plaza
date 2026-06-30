package com.buildsol.wordplaza.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.buildsol.wordplaza.model.AppUiState
import com.buildsol.wordplaza.model.NavItem
import com.buildsol.wordplaza.navigation.CreatePostRoute
import com.buildsol.wordplaza.navigation.HomeScreenRoute
import com.buildsol.wordplaza.navigation.ProfileScreenRoute
import com.buildsol.wordplaza.navigation.SearchScreenRoute


@Composable
fun AppNavigationBar(
    appUiState: AppUiState,
    modifier: Modifier = Modifier
) {
    if (appUiState.hideBottomNavigation) return

    val items = listOf(
        NavItem(HomeScreenRoute, "Feed", Icons.Default.Home),
        NavItem(CreatePostRoute, "Add Word", Icons.AutoMirrored.Filled.NoteAdd),
        NavItem(ProfileScreenRoute, "Profile", Icons.Default.Person)
    )

    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {

        items.forEach { item ->

            val isSelected = appUiState.currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { appUiState.onNavigate(item.route) },

                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },

                label = {
                    Text(item.label)
                },

                alwaysShowLabel = true,

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,

                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,

                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            )
        }
    }
}

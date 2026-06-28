package com.buildsol.wordplaza.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.buildsol.wordplaza.model.AppUiState
import com.buildsol.wordplaza.view.home.HomeScreen
import com.buildsol.wordplaza.view.login.LoginScreen
import com.buildsol.wordplaza.view.onboarding.OnboardingScreen
import com.buildsol.wordplaza.view.profile.FollowListScreen
import com.buildsol.wordplaza.view.profile.ProfileScreen
import com.buildsol.wordplaza.view.profileChange.ProfileUpdate
import com.buildsol.wordplaza.view.search.SearchScreen
import com.buildsol.wordplaza.viewModel.homeScreenViewModel.HomeScreenViewModel
import com.buildsol.wordplaza.viewModel.loginViewModel.LoginViewModel
import com.buildsol.wordplaza.viewModel.onboardingViewModels.OnboardingViewModel
import com.buildsol.wordplaza.viewModel.profileUpdateViewModel.ProfileUpdateViewModel
import com.buildsol.wordplaza.viewModel.profileViewModel.FollowListViewModel
import com.buildsol.wordplaza.viewModel.profileViewModel.ProfileViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNavHost(
    navController: NavHostController,
    startingScreen: GTCAppRoute = OnboardingScreenRoute,
    onSetAppUiState: (AppUiState) -> Unit,
    modifier: Modifier,
) {
    val respectNavController = remember {
        NotableComposeNavController(navController)
    }

    NavHost(
        navController = navController,
        startDestination = startingScreen,
        modifier = modifier,
    ) {
        composable<OnboardingScreenRoute> {
            val viewModel: OnboardingViewModel = appViewModel(
                onSetAppUiState = onSetAppUiState,
                navController = respectNavController
            )
            OnboardingScreen(viewModel = viewModel)
        }

        composable<LoginScreenRoute> {
            val viewModel: LoginViewModel = appViewModel(
                onSetAppUiState = onSetAppUiState,
                navController = respectNavController
            )
            LoginScreen(viewModel = viewModel)
        }

        composable<ProfileUpdateRoute> {
            val viewModel: ProfileUpdateViewModel = appViewModel(
                onSetAppUiState = onSetAppUiState,
                navController = respectNavController
            )
            ProfileUpdate(viewModel = viewModel)
        }

        composable<HomeScreenRoute> {
            val viewModel: HomeScreenViewModel = appViewModel(
                onSetAppUiState = onSetAppUiState,
                navController = respectNavController
            )
            HomeScreen(viewModel = viewModel)
        }

        composable<ProfileScreenRoute> {
            val viewModel: ProfileViewModel = appViewModel(
                onSetAppUiState = onSetAppUiState,
                navController = respectNavController
            )
            ProfileScreen(viewModel = viewModel)
        }

        composable<FollowListRoute> {
            val viewModel: FollowListViewModel = appViewModel(
                onSetAppUiState = onSetAppUiState,
                navController = respectNavController
            )
            FollowListScreen(viewModel = viewModel)
        }

        composable<SearchScreenRoute> {
            SearchScreen()
        }
    }
}

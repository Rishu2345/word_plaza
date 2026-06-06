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
import com.buildsol.wordplaza.view.profileChange.ProfileUpdate
import com.buildsol.wordplaza.viewModel.homeScreenViewModel.HomeScreenViewModel
import com.buildsol.wordplaza.viewModel.loginViewModel.LoginViewModel
import com.buildsol.wordplaza.viewModel.onboardingViewModels.OnboardingViewModel
import com.buildsol.wordplaza.viewModel.profileUpdateViewModel.ProfileUpdateViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNavHost(
    navController: NavHostController,
    onSetAppUiState: (AppUiState) -> Unit,
    modifier: Modifier,
) {
    val respectNavController = remember {
        NotableComposeNavController(navController)
    }

    NavHost(
        navController = navController,
        startDestination = OnboardingScreenRoute,
        modifier = modifier,
    ) {
        composable<OnboardingScreenRoute>{
            val viewModel: OnboardingViewModel = appViewModel(
                onSetAppUiState = onSetAppUiState,
                navController = respectNavController
            )
            OnboardingScreen(viewModel = viewModel)
        }

        composable<LoginScreenRoute>{
            val viewModel: LoginViewModel = appViewModel(
                onSetAppUiState = onSetAppUiState,
                navController = respectNavController
            )
            LoginScreen(viewModel = viewModel)
        }

        composable<ProfileUpdateRoute>{
            val viewModel: ProfileUpdateViewModel = appViewModel(
                onSetAppUiState = onSetAppUiState,
                navController = respectNavController
            )
            ProfileUpdate(viewModel = viewModel)
        }

        composable<HomeScreenRoute> {
            val viewModel: HomeScreenViewModel = appViewModel<HomeScreenViewModel>(
                onSetAppUiState = onSetAppUiState,
                navController = respectNavController
            )
            HomeScreen(viewModel = viewModel)
        }

    }
}
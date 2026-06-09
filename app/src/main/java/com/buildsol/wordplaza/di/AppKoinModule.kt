package com.buildsol.wordplaza.di

import com.buildsol.wordplaza.firebase.authentication.FirebaseAuth
import com.buildsol.wordplaza.firebase.firestore.FireStore
import com.buildsol.wordplaza.view.profileChange.ProfileImageProcessor
import com.buildsol.wordplaza.view.profileChange.ProfileImageStorage
import com.buildsol.wordplaza.viewModel.homeScreenViewModel.HomeScreenViewModel
import com.buildsol.wordplaza.viewModel.loginViewModel.LoginViewModel
import com.buildsol.wordplaza.viewModel.onboardingViewModels.OnboardingViewModel
import com.buildsol.wordplaza.viewModel.profileUpdateViewModel.ProfileUpdateViewModel
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val appKoinModule = module {

    single<Json> {
        Json {
            encodeDefaults = false
            ignoreUnknownKeys = true
        }
    }

    single{
        FirebaseAuth(get())
    }

    single {
        FireStore()
    }

    viewModelOf(::OnboardingViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::ProfileUpdateViewModel)
    viewModelOf(::HomeScreenViewModel)
}

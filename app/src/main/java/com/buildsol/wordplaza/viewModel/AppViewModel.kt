package com.buildsol.wordplaza.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.buildsol.wordplaza.model.AppUiState
import com.buildsol.wordplaza.navigation.CreatePostRoute
import com.buildsol.wordplaza.navigation.GTCAppRoute
import com.buildsol.wordplaza.navigation.HomeScreenRoute
import com.buildsol.wordplaza.navigation.NavCommand
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update

open class AppViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel(){

    val _appUiState = MutableStateFlow(AppUiState())
    val appUiState= _appUiState.asSharedFlow()

    init{
        _appUiState.update {
            it.copy(
                onNavigate = ::onNavigate
            )
        }
    }

    private fun onNavigate(route: GTCAppRoute) {
        if(route != _appUiState.value.currentRoute){
            _appUiState.update {
                it.copy(currentRoute = route)
            }
            if(route == CreatePostRoute){
                _appUiState.update{it.copy(showPostBottomSheet = true)}
            }else{
                _navCommandFlow.tryEmit(NavCommand.Navigate(route))
            }
        }
    }
    protected val _navCommandFlow = MutableSharedFlow<NavCommand>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    val navCommandFlow: Flow<NavCommand> = _navCommandFlow.asSharedFlow()

    private var lastNavResultTimestampCollected: Long =
        savedStateHandle.get<String>(KEY_LAST_COLLECTED_TS)?.toLong() ?: 0L
        set(value) {
            field = value
            savedStateHandle[KEY_LAST_COLLECTED_TS] = value.toString()
        }

    init {
        if (lastNavResultTimestampCollected == 0L)
            lastNavResultTimestampCollected = System.currentTimeMillis()
    }

    companion object {
        const val KEY_LAST_COLLECTED_TS = "collectedTs"
    }

}
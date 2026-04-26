package com.buildsol.wordplaza.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.buildsol.wordplaza.navigation.NavCommand
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

open class AppViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel(){
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
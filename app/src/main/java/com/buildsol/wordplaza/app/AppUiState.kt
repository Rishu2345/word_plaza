package org.example.notable.app

import kotlinx.serialization.Serializable

@Serializable
data class AppUiState(
    val title: String = "Hotel Panel",
    val hideBottomNavigation: Boolean = false,
    val hideAppBar: Boolean = false,
    val previousButtonEnable:Boolean = true,
    val nextButtonEnable:Boolean = true,
    val onPreviousButtonClick:(() -> Unit) = {},
    val onNextButtonClick: (() -> Unit) = {},
    val hidePreviousButton:Boolean = false,
    val hideNextButton: Boolean = false,
    val step:Int = 1,
    val totalSteps:Int = 3,
    val progress: (() -> Float) = {step.toFloat()/totalSteps},
    val showProgress:Boolean = true,
)


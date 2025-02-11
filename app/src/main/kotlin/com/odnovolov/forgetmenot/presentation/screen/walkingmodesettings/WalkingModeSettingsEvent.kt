package com.odnovolov.forgetmenot.presentation.screen.walkingmodesettings

sealed class WalkingModeSettingsEvent {
    object HelpButtonClicked : WalkingModeSettingsEvent()

    class KeyGestureActionSelected(
        val keyGesture: KeyGesture,
        val keyGestureAction: KeyGestureAction
    ) : WalkingModeSettingsEvent()
}
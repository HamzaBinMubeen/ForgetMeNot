package com.odnovolov.forgetmenot.presentation.screen.questiondisplay

sealed class QuestionDisplayEvent {
    object HelpButtonClicked : QuestionDisplayEvent()
    object QuestionDisplaySwitchToggled : QuestionDisplayEvent()
}
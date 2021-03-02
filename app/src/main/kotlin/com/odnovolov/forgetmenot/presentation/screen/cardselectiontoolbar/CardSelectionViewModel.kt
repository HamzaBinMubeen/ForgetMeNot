package com.odnovolov.forgetmenot.presentation.screen.cardselectiontoolbar

import com.odnovolov.forgetmenot.domain.interactor.cardeditor.BatchCardEditor
import com.odnovolov.forgetmenot.domain.interactor.cardeditor.EditableCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CardSelectionViewModel(
    private val batchCardEditorState: BatchCardEditor.State
) {
    val numberOfSelectedCards: Flow<Int> =
        batchCardEditorState.flowOf(BatchCardEditor.State::selectedCards)
            .map { editableCards: Collection<EditableCard> -> editableCards.size }

    val isMarkAsLearnedOptionAvailable: Boolean
        get() = batchCardEditorState.selectedCards.any { editableCard: EditableCard ->
            !editableCard.card.isLearned
        }

    val isMarkAsUnlearnedOptionAvailable: Boolean
        get() = batchCardEditorState.selectedCards.any { editableCard: EditableCard ->
            editableCard.card.isLearned
        }
}
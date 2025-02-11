package com.odnovolov.forgetmenot.persistence.shortterm

import com.odnovolov.forgetmenot.Database
import com.odnovolov.forgetmenot.domain.entity.Card
import com.odnovolov.forgetmenot.domain.entity.GlobalState
import com.odnovolov.forgetmenot.persistence.shortterm.CardAppearanceScreenStateProvider.SerializableState
import com.odnovolov.forgetmenot.presentation.screen.cardappearance.CardAppearanceScreenState
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class CardAppearanceScreenStateProvider(
    json: Json,
    database: Database,
    private val globalState: GlobalState,
    override val key: String = CardAppearanceScreenState::class.qualifiedName!!
) : BaseSerializableStateProvider<CardAppearanceScreenState, SerializableState>(
    json,
    database
) {
    @Serializable
    data class SerializableState(
        val exampleCardIds: List<Long>
    )

    override val serializer = SerializableState.serializer()

    override fun toSerializable(state: CardAppearanceScreenState): SerializableState {
        val exampleCardIds: List<Long> = state.exampleCards.map { card: Card -> card.id }
        return SerializableState(exampleCardIds)
    }

    override fun toOriginal(serializableState: SerializableState): CardAppearanceScreenState {
        val cardIdCardMap: Map<Long, Card> = globalState.decks
            .flatMap { deck -> deck.cards }
            .associateBy { card -> card.id }
        val exampleCards: List<Card> = serializableState.exampleCardIds.map { cardId: Long ->
            cardIdCardMap[cardId] ?: Card(cardId, question = "Question", answer = "Answer")
        }
        return CardAppearanceScreenState(exampleCards)
    }
}
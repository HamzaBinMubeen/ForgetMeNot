package com.odnovolov.forgetmenot.presentation.screen.exercise.exercisecard.quiz

import com.odnovolov.forgetmenot.domain.entity.Card
import com.odnovolov.forgetmenot.domain.interactor.exercise.ExerciseCard
import com.odnovolov.forgetmenot.domain.interactor.exercise.QuizTestExerciseCard
import com.odnovolov.forgetmenot.presentation.common.businessLogicThread
import com.odnovolov.forgetmenot.presentation.common.mapTwoLatest
import com.odnovolov.forgetmenot.presentation.screen.exercise.exercisecard.CardLabel
import com.odnovolov.forgetmenot.presentation.screen.exercise.exercisecard.quiz.VariantStatus.*
import kotlinx.coroutines.flow.*

class QuizTestExerciseCardViewModel(
    initialExerciseCard: QuizTestExerciseCard
) {
    private val exerciseCardFlow = MutableStateFlow(initialExerciseCard)

    fun setExerciseCard(exerciseCard: QuizTestExerciseCard) {
        exerciseCardFlow.value = exerciseCard
    }

    val cardContent: Flow<QuizCardContent> = exerciseCardFlow.flatMapLatest { exerciseCard ->
        val questionFlow = if (exerciseCard.base.isInverted)
            exerciseCard.base.card.flowOf(Card::answer) else
            exerciseCard.base.card.flowOf(Card::question)
        val variantFlows: List<Flow<String?>> = exerciseCard.variants.map { card: Card? ->
            if (card != null) {
                if (exerciseCard.base.isInverted)
                    card.flowOf(Card::question) else
                    card.flowOf(Card::answer)
            } else {
                flowOf(null)
            }
        }
        combine(
            questionFlow,
            combine(variantFlows) { it.toList() },
            ::QuizCardContent
        )
    }
        .distinctUntilChanged()
        .flowOn(businessLogicThread)

    val isQuestionDisplayed: Flow<Boolean> =
        exerciseCardFlow.flatMapLatest { exerciseCard ->
            exerciseCard.base.flowOf(ExerciseCard.Base::isQuestionDisplayed)
        }
            .distinctUntilChanged()
            .flowOn(businessLogicThread)

    fun variantStatus(variantIndex: Int): Flow<VariantStatus> =
        exerciseCardFlow.flatMapLatest { exerciseCard ->
            exerciseCard.base.flowOf(ExerciseCard.Base::isAnswerCorrect)
                .map { isAnswerCorrect: Boolean? ->
                    val isAnswered: Boolean = isAnswerCorrect != null
                    val variantCardId: Long? = exerciseCard.variants[variantIndex]?.id
                    val correctCardId: Long = exerciseCard.base.card.id
                    val selectedVariantIndex: Int? = exerciseCard.selectedVariantIndex
                    when {
                        !isAnswered -> WaitingForAnswer
                        variantCardId == correctCardId && variantIndex == selectedVariantIndex -> Correct
                        variantCardId == correctCardId -> CorrectButNotSelected
                        variantIndex == selectedVariantIndex -> Wrong
                        else -> WrongButNotSelected
                    }
                }
        }
            .distinctUntilChanged()
            .flowOn(businessLogicThread)

    val isAnswered: Flow<Boolean> = exerciseCardFlow.flatMapLatest { exerciseCard ->
        exerciseCard.base.flowOf(ExerciseCard.Base::isAnswerCorrect)
    }
        .map { isAnswerCorrect: Boolean? -> isAnswerCorrect != null }
        .distinctUntilChanged()
        .flowOn(businessLogicThread)

    val isExpired: Flow<Boolean> = exerciseCardFlow.flatMapLatest { exerciseCard ->
        combine(
            exerciseCard.base.flowOf(ExerciseCard.Base::isExpired),
            exerciseCard.base.flowOf(ExerciseCard.Base::isAnswerCorrect)
        ) { isExpired: Boolean, isAnswerCorrect: Boolean? ->
            isExpired && isAnswerCorrect != true
        }
    }
        .distinctUntilChanged()
        .flowOn(businessLogicThread)

    val vibrateCommand: Flow<Unit> = exerciseCardFlow.flatMapLatest { exerciseCard ->
        exerciseCard.base.flowOf(ExerciseCard.Base::isAnswerCorrect)
    }
        .mapTwoLatest { wasCorrect: Boolean?, isCorrectNow: Boolean? ->
            if (wasCorrect == null && isCorrectNow == false) Unit else null
        }
        .filterNotNull()
        .flowOn(businessLogicThread)

    val isLearned: Flow<Boolean> = exerciseCardFlow.flatMapLatest { exerciseCard ->
        exerciseCard.base.card.flowOf(Card::isLearned)
    }
        .distinctUntilChanged()
        .flowOn(businessLogicThread)

    val cardLabel: Flow<CardLabel?> = combine(isLearned, isExpired) { isLearned: Boolean,
                                                                      isExpired: Boolean ->
        when {
            isLearned -> CardLabel.Learned
            isExpired -> CardLabel.Expired
            else -> null
        }
    }
        .distinctUntilChanged()
        .flowOn(businessLogicThread)
}
package com.odnovolov.forgetmenot.presentation.screen.deckeditor.decksettings

import android.content.Context
import com.odnovolov.forgetmenot.R
import com.odnovolov.forgetmenot.domain.entity.*
import com.odnovolov.forgetmenot.domain.entity.PronunciationEvent.*
import com.odnovolov.forgetmenot.presentation.screen.intervals.DisplayedInterval

enum class DisplayingDeckSetting(
    val titleRes: Int,
    val getDisplayText: (exercisePreference: ExercisePreference, context: Context) -> String
) {
    RANDOM_ORDER(
        titleRes = R.string.deck_settings_item_random_order,
        getDisplayText = { exercisePreference, context ->
            getRandomOrderDisplayText(exercisePreference.randomOrder, context)
        }
    ),
    PRONUNCIATION(
        titleRes = R.string.deck_settings_item_pronunciation,
        getDisplayText = { exercisePreference, context ->
            getPronunciationDisplayText(exercisePreference.pronunciation, context)
        }
    ),
    CARD_INVERSION(
        titleRes = R.string.deck_settings_item_card_inversion,
        getDisplayText = { exercisePreference, context ->
            getCardInversionDisplayText(exercisePreference.cardInversion, context)
        }
    ),
    QUESTION_DISPLAY(
        titleRes = R.string.deck_settings_item_question_display,
        getDisplayText = { exercisePreference, context ->
            getQuestionDisplayDisplayText(exercisePreference.isQuestionDisplayed, context)
        }
    ),
    TESTING_METHOD(
        titleRes = R.string.deck_settings_item_testing_method,
        getDisplayText = { exercisePreference, context ->
            getTestingMethodDisplayText(exercisePreference.testingMethod, context)
        }
    ),
    INTERVALS(
        titleRes = R.string.deck_settings_item_intervals,
        getDisplayText = { exercisePreference, context ->
            getIntervalsDisplayText(exercisePreference.intervalScheme, context)
        }
    ),
    MOTIVATIONAL_TIMER(
        titleRes = R.string.deck_settings_item_motivational_timer,
        getDisplayText = { exercisePreference, context ->
            getMotivationalTimerDisplayText(exercisePreference.timeForAnswer, context)
        }
    ),
    PRONUNCIATION_PLAN(
        titleRes = R.string.deck_settings_item_pronunciation_plan,
        getDisplayText = { exercisePreference, context ->
            getPronunciationPlanDisplayText(exercisePreference.pronunciationPlan, context)
        }
    )
}

fun getRandomOrderDisplayText(randomOrder: Boolean, context: Context): String {
    return context.getString(
        if (randomOrder)
            R.string.on else
            R.string.off
    )
}


fun getPronunciationDisplayText(pronunciation: Pronunciation, context: Context): String {
    return buildString {
        append(
            pronunciation.questionLanguage?.displayLanguage
                ?: context.getString(R.string.default_language)
        )
        if (pronunciation.questionAutoSpeaking) {
            append(" (A)")
        }
        append("  |  ")
        append(
            pronunciation.answerLanguage?.displayLanguage
                ?: context.getString(R.string.default_language)
        )
        if (pronunciation.answerAutoSpeaking) {
            append(" (A)")
        }
    }
}

fun getCardInversionDisplayText(cardInversion: CardInversion, context: Context): String {
    return context.getString(
        when (cardInversion) {
            CardInversion.Off -> R.string.item_card_inversion_off
            CardInversion.On -> R.string.item_card_inversion_on
            CardInversion.EveryOtherLap -> R.string.item_card_inversion_every_other_lap
            CardInversion.Randomly -> R.string.item_card_inversion_randomly
        }
    )
}

fun getQuestionDisplayDisplayText(isQuestionDisplayed: Boolean, context: Context): String {
    return context.getString(
        if (isQuestionDisplayed)
            R.string.on else
            R.string.off
    )
}

fun getTestingMethodDisplayText(testingMethod: TestingMethod, context: Context): String {
    return context.getString(
        when (testingMethod) {
            TestingMethod.Off -> R.string.testing_method_without_testing
            TestingMethod.Manual -> R.string.testing_method_self_testing
            TestingMethod.Quiz -> R.string.testing_method_testing_with_variants
            TestingMethod.Entry -> R.string.testing_method_spell_check
        }
    )
}

fun getIntervalsDisplayText(intervalScheme: IntervalScheme?, context: Context): String {
    return if (intervalScheme == null) {
        context.getString(R.string.off)
    } else {
        intervalScheme.intervals.joinToString(separator = "  ") { interval: Interval ->
            DisplayedInterval.fromDateTimeSpan(interval.value).getAbbreviation(context)
        }
    }
}

fun getMotivationalTimerDisplayText(timeForAnswer: Int, context: Context): String {
    return if (timeForAnswer == NOT_TO_USE_TIMER)
        context.getString(R.string.off) else
        context.getString(R.string.time_for_answer, timeForAnswer)
}

fun getPronunciationPlanDisplayText(
    pronunciationPlan: PronunciationPlan,
    context: Context
): String {
    return pronunciationPlan.pronunciationEvents
        .joinToString(separator = "  ") { pronunciationEvent: PronunciationEvent ->
            when (pronunciationEvent) {
                SpeakQuestion -> context.getString(R.string.speak_event_abbr_speak_question)
                SpeakAnswer -> context.getString(R.string.speak_event_abbr_speak_answer)
                is Delay -> context.getString(
                    R.string.speak_event_abbr_delay,
                    pronunciationEvent.timeSpan.seconds.toInt()
                )
            }
        }
}
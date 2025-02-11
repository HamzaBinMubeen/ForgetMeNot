package com.odnovolov.forgetmenot.persistence

object DbKeys {
    const val CARD_FILTER_FOR_AUTOPLAY_ARE_CARDS_AVAILABLE_FOR_EXERCISE_INCLUDED = 0L
    const val CARD_FILTER_FOR_AUTOPLAY_ARE_AWAITING_CARDS_INCLUDED = 1L
    const val CARD_FILTER_FOR_AUTOPLAY_ARE_LEARNED_CARDS_INCLUDED = 2L
    const val CARD_FILTER_FOR_AUTOPLAY_GRADE_MIN = 3L
    const val CARD_FILTER_FOR_AUTOPLAY_GRADE_MAX = 4L
    const val CARD_FILTER_FOR_AUTOPLAY_LAST_TESTED_FROM_TIME_AGO = 5L
    const val CARD_FILTER_FOR_AUTOPLAY_LAST_TESTED_TO_TIME_AGO = 6L
    const val IS_WALKING_MODE_ENABLED = 7L
    const val IS_INFINITE_PLAYBACK_ENABLED = 8L // outdated
    const val ARE_INITIAL_DECKS_ADDED = 9L
    const val DECK_SORTING_CRITERION = 10L // outdated
    const val DECK_SORTING_DIRECTION = 11L // outdated
    const val DISPLAY_ONLY_DECKS_AVAILABLE_FOR_EXERCISE = 12L // outdated
    const val IS_FULLSCREEN_ENABLED_IN_EXERCISE = 13L
    const val IS_FULLSCREEN_ENABLED_IN_CARD_PLAYER = 14L
    const val IS_FULLSCREEN_ENABLED_IN_OTHER_PLACES = 15L
    const val NUMBER_OF_LAPS_IN_PLAYER = 16L
    const val LAST_USED_ENCODING_NAME = 17L
    const val LAST_USED_FILE_FORMAT_ID_FOR_TXT = 18L
    const val LAST_USED_FILE_FORMAT_ID_FOR_CSV = 19L
    const val LAST_USED_FILE_FORMAT_ID_FOR_TSV = 20L
    const val FAVORITE_LANGUAGES = 21L
    const val LAST_USED_LANGUAGE_1 = 22L
    const val LAST_USED_LANGUAGE_2 = 23L
    const val QUESTION_TEXT_ALIGNMENT = 24L
    const val QUESTION_TEXT_SIZE = 25L
    const val ANSWER_TEXT_ALIGNMENT = 26L
    const val ANSWER_TEXT_SIZE = 27L
}
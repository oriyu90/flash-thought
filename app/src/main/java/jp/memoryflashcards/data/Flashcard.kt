package jp.memoryflashcards.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import jp.memoryflashcards.R

data class Flashcard(
    // 問題名はロケール毎のリソースに持たせ、日本語/英語どちらのUIでも表示できるようにする。
    @StringRes val nameRes: Int,
    @DrawableRes val images: List<Int>
)

val flashcards = listOf(
    Flashcard(R.string.card_name_01, listOf(R.drawable.card_01_1, R.drawable.card_01_2, R.drawable.card_01_3)),
    Flashcard(R.string.card_name_02, listOf(R.drawable.card_02_1, R.drawable.card_02_2, R.drawable.card_02_3)),
    Flashcard(R.string.card_name_03, listOf(R.drawable.card_03_1, R.drawable.card_03_2, R.drawable.card_03_3)),
    Flashcard(R.string.card_name_04, listOf(R.drawable.card_04_1, R.drawable.card_04_2, R.drawable.card_04_3)),
    Flashcard(R.string.card_name_05, listOf(R.drawable.card_05_1, R.drawable.card_05_2, R.drawable.card_05_3)),
    Flashcard(R.string.card_name_06, listOf(R.drawable.card_06_1, R.drawable.card_06_2, R.drawable.card_06_3)),
    Flashcard(R.string.card_name_07, listOf(R.drawable.card_07_1, R.drawable.card_07_2, R.drawable.card_07_3)),
    Flashcard(R.string.card_name_08, listOf(R.drawable.card_08_1, R.drawable.card_08_2, R.drawable.card_08_3)),
    Flashcard(R.string.card_name_09, listOf(R.drawable.card_09_1, R.drawable.card_09_2, R.drawable.card_09_3)),
    Flashcard(R.string.card_name_10, listOf(R.drawable.card_10_1, R.drawable.card_10_2, R.drawable.card_10_3))
)

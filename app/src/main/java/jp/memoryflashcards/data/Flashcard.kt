package jp.memoryflashcards.data

import androidx.annotation.DrawableRes
import jp.memoryflashcards.R

data class Flashcard(
    val name: String,
    @DrawableRes val images: List<Int>
)

val flashcards = listOf(
    Flashcard("パプリカ", listOf(R.drawable.card_01_1, R.drawable.card_01_2, R.drawable.card_01_3)),
    Flashcard("バレーボール", listOf(R.drawable.card_02_1, R.drawable.card_02_2, R.drawable.card_02_3)),
    Flashcard("鉛筆", listOf(R.drawable.card_03_1, R.drawable.card_03_2, R.drawable.card_03_3)),
    Flashcard("ボールペン", listOf(R.drawable.card_04_1, R.drawable.card_04_2, R.drawable.card_04_3)),
    Flashcard("色鉛筆", listOf(R.drawable.card_05_1, R.drawable.card_05_2, R.drawable.card_05_3)),
    Flashcard("リモコン", listOf(R.drawable.card_06_1, R.drawable.card_06_2, R.drawable.card_06_3)),
    Flashcard("エアコン", listOf(R.drawable.card_07_1, R.drawable.card_07_2, R.drawable.card_07_3)),
    Flashcard("ガスコンロ", listOf(R.drawable.card_08_1, R.drawable.card_08_2, R.drawable.card_08_3)),
    Flashcard("トースター", listOf(R.drawable.card_09_1, R.drawable.card_09_2, R.drawable.card_09_3)),
    Flashcard("洗面器", listOf(R.drawable.card_10_1, R.drawable.card_10_2, R.drawable.card_10_3))
)

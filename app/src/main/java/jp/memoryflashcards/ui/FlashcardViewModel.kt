package jp.memoryflashcards.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import jp.memoryflashcards.data.Flashcard
import jp.memoryflashcards.data.RecentScore
import jp.memoryflashcards.data.ScoreRepository
import jp.memoryflashcards.data.flashcards
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * セッションの状態。未開始の状態と範囲外の添字を常に安全に扱い、
 * 画面の再生成や連打で例外（IndexOutOfBounds）を起こさない。
 */
data class QuizState(
    val cards: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val correctCount: Int = 0
) {
    val isStarted: Boolean get() = cards.isNotEmpty()
    val total: Int get() = cards.size
    val currentCard: Flashcard?
        get() = cards.getOrNull(currentIndex)
    val isFinished: Boolean get() = isStarted && currentIndex >= cards.lastIndex
}

class FlashcardViewModel(application: Application) : AndroidViewModel(application) {
    private val scoreRepository = ScoreRepository(application)

    val recentScore: StateFlow<RecentScore?> = scoreRepository.recentScore.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), null
    )

    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state.asStateFlow()

    fun beginSession() {
        _state.value = QuizState(cards = flashcards.shuffled())
    }

    /** 回答を記録する。セッションが終了したら true を返す。 */
    fun recordAnswer(isCorrect: Boolean): Boolean {
        val current = _state.value
        if (!current.isStarted) return true
        val correct = current.correctCount + if (isCorrect) 1 else 0
        val finished = current.currentIndex >= current.cards.lastIndex
        _state.value = current.copy(
            correctCount = correct,
            currentIndex = if (finished) current.currentIndex else current.currentIndex + 1
        )
        if (finished) {
            viewModelScope.launch {
                scoreRepository.save(RecentScore(correct, current.cards.size, System.currentTimeMillis()))
            }
        }
        return finished
    }
}

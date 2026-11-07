package jp.memoryflashcards.data

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "recent_score")

data class RecentScore(val correct: Int, val total: Int, val completedAt: Long)

class ScoreRepository(private val context: Context) {
    private val correctKey = intPreferencesKey("correct")
    private val totalKey = intPreferencesKey("total")
    private val completedAtKey = longPreferencesKey("completed_at")

    val recentScore: Flow<RecentScore?> = context.dataStore.data.map { values ->
        val completedAt = values[completedAtKey] ?: return@map null
        RecentScore(values[correctKey] ?: 0, values[totalKey] ?: 0, completedAt)
    }

    suspend fun save(score: RecentScore) {
        context.dataStore.edit {
            it[correctKey] = score.correct
            it[totalKey] = score.total
            it[completedAtKey] = score.completedAt
        }
    }
}

package jp.memoryflashcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import jp.memoryflashcards.ui.FlashcardApp
import jp.memoryflashcards.ui.theme.MemoryFlashcardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MemoryFlashcardsTheme { FlashcardApp() }
        }
    }
}

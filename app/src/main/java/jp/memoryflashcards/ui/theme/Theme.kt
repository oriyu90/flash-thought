package jp.memoryflashcards.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 常時ダークの配色。文字と背景のコントラストは WCAG AA（本文 4.5:1 以上）を満たす。
 * 端末の「文字サイズ」設定は Compose が自動で反映するため、ここでは倍率を固定しない。
 */
private val DarkScheme = darkColorScheme(
    primary = Color(0xFFFFB95E),
    onPrimary = Color(0xFF422B00),
    primaryContainer = Color(0xFF5E4300),
    onPrimaryContainer = Color(0xFFFFDDB5),
    secondary = Color(0xFFB9CBEA),
    onSecondary = Color(0xFF233148),
    secondaryContainer = Color(0xFF3A485F),
    onSecondaryContainer = Color(0xFFD7E2FF),
    tertiary = Color(0xFFB9D398),
    onTertiary = Color(0xFF273516),
    tertiaryContainer = Color(0xFF3D4C2A),
    onTertiaryContainer = Color(0xFFD5EEB2),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF101418),
    onBackground = Color(0xFFE1E2E8),
    surface = Color(0xFF101418),
    onSurface = Color(0xFFE1E2E8),
    surfaceVariant = Color(0xFF44474E),
    onSurfaceVariant = Color(0xFFC4C6D0),
    outline = Color(0xFF8E9099)
)

/** 高齢者向けに、本文・見出しを大きめにした文字設定。 */
private val AppTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 64.sp, lineHeight = 72.sp),
    displaySmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 44.sp, lineHeight = 52.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 32.sp, lineHeight = 40.sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 30.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 28.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 24.sp)
)

@Composable
fun MemoryFlashcardsTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkScheme, typography = AppTypography, content = content)
}

package jp.memoryflashcards.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import jp.memoryflashcards.R
import jp.memoryflashcards.data.Flashcard
import jp.memoryflashcards.data.RecentScore
import java.text.DateFormat
import java.util.Date

private enum class Destination { Home, Quiz, Result }

private val ContentMaxWidth = 560.dp
private val ButtonMinHeight = 64.dp

@Composable
fun FlashcardApp(viewModel: FlashcardViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var destinationName by rememberSaveable { mutableStateOf(Destination.Home.name) }
    // セッションが無いのに出題・結果画面へ来た場合は、クラッシュせずホームへ戻す。
    val destination = when (Destination.valueOf(destinationName)) {
        Destination.Quiz, Destination.Result -> if (state.isStarted) Destination.valueOf(destinationName) else Destination.Home
        Destination.Home -> Destination.Home
    }
    Crossfade(targetState = destination, animationSpec = tween(220), label = "screen") { screen ->
        when (screen) {
            Destination.Home -> HomeScreen(
                recentScore = viewModel.recentScore.collectAsStateWithLifecycle().value,
                onStart = { viewModel.beginSession(); destinationName = Destination.Quiz.name }
            )
            Destination.Quiz -> QuizScreen(
                state = state,
                onExit = { destinationName = Destination.Home.name },
                onAnswer = { correct ->
                    if (viewModel.recordAnswer(correct)) destinationName = Destination.Result.name
                }
            )
            Destination.Result -> ResultScreen(
                correct = state.correctCount,
                total = state.total,
                onHome = { destinationName = Destination.Home.name },
                onAgain = { viewModel.beginSession(); destinationName = Destination.Quiz.name }
            )
        }
    }
}

@Composable
private fun AppPage(content: @Composable () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(Modifier.windowInsetsPadding(WindowInsets.safeDrawing).fillMaxSize()) { content() }
    }
}

@Composable
private fun HomeScreen(recentScore: RecentScore?, onStart: () -> Unit) = AppPage {
    // 文字サイズを大きくしても内容が切れないよう、縦スクロールを許可する。
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.home_tagline),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(40.dp))
        ScoreCard(recentScore, Modifier.widthIn(max = ContentMaxWidth).fillMaxWidth())
        Spacer(Modifier.height(28.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.widthIn(max = ContentMaxWidth).fillMaxWidth().sizeIn(minHeight = 72.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(stringResource(R.string.home_start), style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.home_hint),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = ContentMaxWidth)
        )
    }
}

@Composable
private fun ScoreCard(score: RecentScore?, modifier: Modifier = Modifier) {
    Card(modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.home_recent_score),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (score == null) {
                Text(
                    text = stringResource(R.string.home_no_score),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val percent = if (score.total == 0) 0 else score.correct * 100 / score.total
                Text(
                    text = stringResource(R.string.home_score_value, score.correct, score.total),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.home_score_percent, percent),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(score.completedAt)),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun QuizScreen(state: QuizState, onExit: () -> Unit, onAnswer: (Boolean) -> Unit) = AppPage {
    val card = state.currentCard ?: return@AppPage
    var answerVisible by rememberSaveable(state.currentIndex) { mutableStateOf(false) }
    BackHandler(enabled = answerVisible) { answerVisible = false }
    BackHandler(enabled = !answerVisible) { onExit() }
    val configuration = LocalConfiguration.current
    val isWide = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ||
        configuration.screenWidthDp >= 600
    if (isWide) {
        Row(Modifier.fillMaxSize().padding(24.dp), horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            CardImageArea(card, answerVisible, Modifier.weight(1f).fillMaxHeight())
            QuizControls(
                number = state.currentIndex + 1,
                total = state.total,
                answerVisible = answerVisible,
                onShowAnswer = { answerVisible = true },
                onAnswer = onAnswer,
                modifier = Modifier.weight(0.82f).fillMaxHeight()
            )
        }
    } else {
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            CardImageArea(card, answerVisible, Modifier.weight(1f).fillMaxWidth())
            QuizControls(
                number = state.currentIndex + 1,
                total = state.total,
                answerVisible = answerVisible,
                onShowAnswer = { answerVisible = true },
                onAnswer = onAnswer,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CardImageArea(card: Flashcard, answerVisible: Boolean, modifier: Modifier) {
    key(card.nameRes) {
        val pagerState = rememberPagerState(pageCount = { card.images.size })
        Box(modifier.clip(RoundedCornerShape(24.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                Image(
                    painter = painterResource(card.images[page]),
                    // 答えを見せる前は、読み上げで名前が漏れないよう汎用の説明にする。
                    contentDescription = stringResource(R.string.quiz_image_description, page + 1, card.images.size),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(12.dp)
                )
            }
            Text(
                text = stringResource(R.string.quiz_image_page, pagerState.currentPage + 1, card.images.size),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = .58f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            AnimatedVisibility(
                visible = answerVisible,
                enter = fadeIn(tween(250)),
                exit = fadeOut(tween(150)),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    Modifier.fillMaxSize().background(MaterialTheme.colorScheme.scrim.copy(alpha = .62f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.quiz_answer_label),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = stringResource(card.nameRes),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.displaySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizControls(
    number: Int,
    total: Int,
    answerVisible: Boolean,
    onShowAnswer: () -> Unit,
    onAnswer: (Boolean) -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.quiz_progress, number, total),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            LinearProgressIndicator(
                progress = { number.toFloat() / total.coerceAtLeast(1) },
                modifier = Modifier.weight(1f)
            )
        }
        AnimatedVisibility(visible = !answerVisible, enter = fadeIn(tween(200)), exit = fadeOut(tween(150))) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = stringResource(R.string.quiz_instruction),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Button(
                    onClick = onShowAnswer,
                    modifier = Modifier.fillMaxWidth().sizeIn(minHeight = ButtonMinHeight)
                ) {
                    Text(stringResource(R.string.quiz_show_answer), style = MaterialTheme.typography.titleLarge)
                }
            }
        }
        AnimatedVisibility(visible = answerVisible, enter = fadeIn(tween(250)), exit = fadeOut(tween(150))) {
            AnswerPanel(onAnswer)
        }
    }
}

@Composable
private fun AnswerPanel(onAnswer: (Boolean) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = stringResource(R.string.answer_question),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Button(
                onClick = { onAnswer(true) },
                modifier = Modifier.fillMaxWidth().sizeIn(minHeight = ButtonMinHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                Text(stringResource(R.string.answer_correct), style = MaterialTheme.typography.titleLarge)
            }
            FilledTonalButton(
                onClick = { onAnswer(false) },
                modifier = Modifier.fillMaxWidth().sizeIn(minHeight = ButtonMinHeight)
            ) {
                Text(stringResource(R.string.answer_again), style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
private fun ResultScreen(correct: Int, total: Int, onHome: () -> Unit, onAgain: () -> Unit) = AppPage {
    val percent = if (total == 0) 0 else correct * 100 / total
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.result_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        Text(
            text = stringResource(R.string.result_score, correct, total),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.result_percent, percent),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(R.string.result_saved),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp).widthIn(max = ContentMaxWidth)
        )
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = onAgain,
            modifier = Modifier.widthIn(max = ContentMaxWidth).fillMaxWidth().sizeIn(minHeight = ButtonMinHeight)
        ) {
            Text(stringResource(R.string.result_again), style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(14.dp))
        FilledTonalButton(
            onClick = onHome,
            modifier = Modifier.widthIn(max = ContentMaxWidth).fillMaxWidth().sizeIn(minHeight = ButtonMinHeight)
        ) {
            Text(stringResource(R.string.result_home), style = MaterialTheme.typography.titleLarge)
        }
    }
}

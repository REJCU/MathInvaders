package com.example.thismathinvaders.game.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thismathinvaders.game.GameCanvas
import com.example.thismathinvaders.game.GameViewModel
import com.example.thismathinvaders.game.data.GameSettings
import com.example.thismathinvaders.game.data.GameStatus
import com.example.thismathinvaders.game.data.SoundManager
import com.example.thismathinvaders.ui.components.GameOverOverlay
import com.example.thismathinvaders.ui.components.TargetAnswerBox

@Composable
fun MathInvadersScreen(
    viewModel: GameViewModel,
    difficulty: String = "default",
    settings: GameSettings = GameSettings(),
    onExitToMenu: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    DisposableEffect(Unit) {
        soundManager.startMusic(settings.musicVolume)
        onDispose {
            soundManager.pauseMusic()
            soundManager.release()
        }
    }

    LaunchedEffect(settings.musicVolume) {
        soundManager.setMusicVolume(settings.musicVolume)
    }


    // track previous value fot playing sound when on hit
    var previousCorrectHits by remember { mutableIntStateOf(uiState.correctHits) }
    var previousIncorrectHits by remember { mutableIntStateOf(uiState.incorrectHits) }


    LaunchedEffect(uiState.correctHits) {
        if (settings.soundVolume > 0f && uiState.correctHits > previousCorrectHits) {
            soundManager.playCorrectHit(settings.soundVolume)
        }
        previousCorrectHits = uiState.correctHits
    }

    LaunchedEffect(uiState.incorrectHits) {
        if (settings.soundVolume > 0f && uiState.incorrectHits > previousIncorrectHits) {
            soundManager.playIncorrectHit(settings.soundVolume)
        }
        previousIncorrectHits = uiState.incorrectHits
    }

    LaunchedEffect(uiState.status) {
        if (settings.soundVolume > 0f && uiState.status == GameStatus.GAME_OVER) {
            soundManager.playGameOver(settings.musicVolume)
        }
    }


    remember(difficulty, settings) {
        viewModel.setDifficulty(difficulty)
        viewModel.updateSettings(settings)
        true
    }

    Box(modifier = modifier.fillMaxSize()) {
        GameCanvas(
            uiState = uiState,
            onSizeReady = { w, h -> viewModel.initScreenBounds(w, h) },
            onShipMove = { x -> viewModel.updateShipPosition(x) }
        )

        TargetAnswerBox(
            targetAnswer = uiState.targetAnswer,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
        )

        Button(
            onClick = { viewModel.fireProjectile() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 32.dp)
                .size(88.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
        ) {
            Text("Fire", fontSize = 32.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Score: ${uiState.score}",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Lives: ${if (uiState.lives >= Int.MAX_VALUE) "Unlimited" 
                    else uiState.lives.toString()}",
                color = Color.Red,
                style = MaterialTheme.typography.titleLarge
            )
        }
        GameOverOverlay(
            visible = uiState.status == GameStatus.GAME_OVER,
            finalScore = uiState.score ,
            onRestart = { viewModel.restartGame() },
            onExit = onExitToMenu,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
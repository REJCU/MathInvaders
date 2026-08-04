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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thismathinvaders.game.GameCanvas
import com.example.thismathinvaders.game.GameViewModel
import com.example.thismathinvaders.game.data.GameSettings
import com.example.thismathinvaders.game.data.GameStatus
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
                text = "Lives: ${uiState.lives}",
                color = Color.Red,
                style = MaterialTheme.typography.titleLarge
            )
        }

        AnimatedVisibility(
            visible = uiState.status == GameStatus.GAME_OVER,
            enter = fadeIn(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "GAME OVER",
                        color = Color.Red,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = "Final Score: ${uiState.score}",
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Row(
                        modifier = Modifier.padding(top = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(onClick = { viewModel.restartGame() }) {
                            Text("Play Again")
                        }

                        OutlinedButton(onClick = onExitToMenu) {
                            Text("Exit Menu")
                        }
                    }
                }
            }
        }
    }
}
package com.example.thismathinvaders.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.unit.dp
import com.example.thismathinvaders.ui.components.DifficultyButton

@Composable
fun LandingPageScreen(
    onNavigateToGame : (Route.Game) ->  Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "MATH INVADERS"
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
             Arrangement.spacedBy(8.dp)
        ) {
            DifficultyButton(
                "Easy",
                MaterialTheme.colorScheme.primary,
                onClick = { onNavigateToGame(Route.Game(difficulty = "Easy")) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            DifficultyButton(
                "Medium",
                MaterialTheme.colorScheme.primary,
                onClick = { onNavigateToGame(Route.Game(difficulty = "Medium")) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            DifficultyButton(
                "Hard",
                MaterialTheme.colorScheme.primary,
                onClick = { onNavigateToGame(Route.Game(difficulty = "Hard")) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            DifficultyButton(
                "Endless",
                MaterialTheme.colorScheme.primary,
                onClick = { onNavigateToGame(Route.Game(difficulty = "Hard")) }
            )
        }
    }
}
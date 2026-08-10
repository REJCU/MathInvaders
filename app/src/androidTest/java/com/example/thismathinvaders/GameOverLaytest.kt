package com.example.thismathinvaders

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.thismathinvaders.ui.components.GameOverOverlay
import org.junit.Rule
import org.junit.Test

class GameOverOverlayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun overlayIsHiddenWhenNotVisible() {
        composeTestRule.setContent {
            GameOverOverlay(
                visible = false,
                finalScore = 100,
                onRestart = {},
                onExit = {}
            )
        }

        composeTestRule.onNodeWithText("GAME OVER").assertDoesNotExist()
    }

    @Test
    fun overlayShowsGameOverAndFinalScoreWhenVisible() {
        composeTestRule.setContent {
            GameOverOverlay(
                visible = true,
                finalScore = 450,
                onRestart = {},
                onExit = {}
            )
        }

        composeTestRule.onNodeWithText("GAME OVER").assertIsDisplayed()
        composeTestRule.onNodeWithText("Final Score: 450").assertIsDisplayed()
    }

    @Test
    fun tappingPlayAgainInvokesOnRestart() {
        var restartCalled = false

        composeTestRule.setContent {
            GameOverOverlay(
                visible = true,
                finalScore = 100,
                onRestart = { restartCalled = true },
                onExit = {}
            )
        }

        composeTestRule.onNodeWithText("Play Again").performClick()
        assert(restartCalled) { "Expected onRestart to be called after tapping Play Again" }
    }

    @Test
    fun tappingExitMenuInvokesOnExit() {
        var exitCalled = false

        composeTestRule.setContent {
            GameOverOverlay(
                visible = true,
                finalScore = 100,
                onRestart = {},
                onExit = { exitCalled = true }
            )
        }

        composeTestRule.onNodeWithText("Exit Menu").performClick()
        assert(exitCalled) { "Expected onExit to be called after tapping Exit Menu" }
    }
}
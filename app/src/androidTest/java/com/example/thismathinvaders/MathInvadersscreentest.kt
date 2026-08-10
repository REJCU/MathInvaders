package com.example.thismathinvaders

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.thismathinvaders.game.GameViewModel
import com.example.thismathinvaders.game.ui.MathInvadersScreen
import com.example.thismathinvaders.repository.AppDatabase
import com.example.thismathinvaders.repository.GameRepository
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MathInvadersScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun initialScreenShowsFireButtonAndStartingScoreAndLives() {
        val viewModel = GameViewModel(GameRepository(database.gameDao()))

        composeTestRule.setContent {
            MathInvadersScreen(viewModel = viewModel, difficulty = "easy")
        }

        composeTestRule.onNodeWithText("Fire").assertIsDisplayed()
        composeTestRule.onNodeWithText("Score: 0").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lives: 3").assertIsDisplayed()
    }
}
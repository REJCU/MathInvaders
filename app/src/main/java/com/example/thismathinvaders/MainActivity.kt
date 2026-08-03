package com.example.thismathinvaders

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.thismathinvaders.ui.theme.ThisMathInvadersTheme
import com.example.thismathinvaders.navigation.Route
import com.example.thismathinvaders.navigation.SetupNavGraph
import com.example.thismathinvaders.repository.AppDatabase
import com.example.thismathinvaders.repository.GameRepository

class MainActivity : ComponentActivity() {
    lateinit var gameRepository: GameRepository
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)

        gameRepository = GameRepository(database.gameDao())

        enableEdgeToEdge()
        setContent {
            ThisMathInvadersTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    navController,
                    gameRepository)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ThisMathInvadersTheme {
        Greeting("Android")
    }
}
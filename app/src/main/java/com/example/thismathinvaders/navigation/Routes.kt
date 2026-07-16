package com.example.thismathinvaders.navigation

import kotlinx.serialization.Serializable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelRoute(
    val name: String,
    val route: Route,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    TopLevelRoute("Home", Route.Landing, Icons.Default.Home),
    TopLevelRoute("Stats", Route.Stats, Icons.Default.Person),
    TopLevelRoute("Settings", Route.Settings, Icons.Default.Settings)
)

sealed interface Route {
    @Serializable
    data object Landing : Route

    @Serializable
    data class Game(val difficulty: String) : Route

    @Serializable
    data object Settings : Route

    @Serializable
    data object Stats : Route
}
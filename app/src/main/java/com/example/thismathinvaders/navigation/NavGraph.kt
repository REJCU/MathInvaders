package com.example.thismathinvaders.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.example.thismathinvaders.game.GameScreen
import com.example.thismathinvaders.navigation.LandingPageScreen


@Composable
fun SetupNavGraph(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // hide bottom bar if the user is on the game screen
    val showBottomBar = currentDestination?.hasRoute<Route.Game>() == false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination.hierarchy.any {
                            it.hasRoute(item.route::class)
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            label = { Text(item.name) },
                            icon = { Icon(item.icon, contentDescription = item.name) },
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Landing,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Route.Landing> {
                LandingPageScreen(
                    onNavigateToGame = { gameRoute->
                        navController.navigate(gameRoute)
                    }
                )
            }

            composable<Route.Game> { backStackEntry ->
                val gameRoute = backStackEntry.toRoute<Route.Game>()
                GameScreen(
                    difficulty = gameRoute.difficulty,
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable<Route.Settings> {
                PlaceholderScreen(title = "Settings Screen")
            }

            composable<Route.Stats> {
                PlaceholderScreen(title = "User Statistics Screen")
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(text = title)
    }
}
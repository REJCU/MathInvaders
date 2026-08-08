package com.example.thismathinvaders.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.example.thismathinvaders.ViewModel.UserStatsViewModel
import com.example.thismathinvaders.game.GameViewModel
import com.example.thismathinvaders.game.ui.MathInvadersScreen
import com.example.thismathinvaders.repository.GameRepository
import androidx.lifecycle.viewmodel.initializer
import com.example.thismathinvaders.ViewModel.SettingsViewModel
import com.example.thismathinvaders.game.data.GameSettings

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    repository: GameRepository
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.hasRoute<Route.Game>() != true

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                SettingsViewModel(repository)
            }
        }
    )
    val gameSettings by settingsViewModel.settings.collectAsState()


    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.hasRoute(item.route::class)
                        } == true

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
                    onNavigateToGame = { gameRoute ->
                        navController.navigate(gameRoute)
                    }
                )
            }

            composable<Route.Game> { backStackEntry ->
                val gameRoute = backStackEntry.toRoute<Route.Game>()

                val gameViewModel: GameViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer {
                            GameViewModel(repository)
                        }
                    }
                )

                MathInvadersScreen(
                    viewModel = gameViewModel,
                    difficulty = gameRoute.difficulty,
                    settings = gameSettings,
                    onExitToMenu = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            composable<Route.Settings> {
                SettingScreen(viewModel = settingsViewModel)
            }


            composable<Route.Stats> {
                val statsViewModel: UserStatsViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer {
                            UserStatsViewModel(repository)
                        }
                    }
                )

                UserStatsScreen(viewModel = statsViewModel)
            }
        }
    }
}
package org.openscholar.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun OpenScholarNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            org.openscholar.app.presentation.screens.home.HomeScreen()
        }
        composable(Screen.Library.route) {
            org.openscholar.app.presentation.screens.library.LibraryScreen()
        }
        composable(Screen.Research.route) {
            org.openscholar.app.presentation.screens.research.ResearchScreen()
        }
        composable(Screen.Chat.route) {
            org.openscholar.app.presentation.screens.chat.ChatScreen()
        }
        composable(Screen.KnowledgeGraph.route) {
            org.openscholar.app.presentation.screens.knowledgegraph.KnowledgeGraphScreen()
        }
        composable(Screen.Tasks.route) {
            org.openscholar.app.presentation.screens.tasks.TasksScreen()
        }
        composable(Screen.Settings.route) {
            org.openscholar.app.presentation.screens.settings.SettingsScreen()
        }
    }
}

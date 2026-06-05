package org.openscholar.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Task
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Library : Screen("library", "Library", Icons.Default.LibraryBooks)
    data object Research : Screen("research", "Research", Icons.Default.QuestionAnswer)
    data object Chat : Screen("chat", "Chat", Icons.Default.Chat)
    data object KnowledgeGraph : Screen("knowledge_graph", "Knowledge Graph", Icons.Default.AccountTree)
    data object Tasks : Screen("tasks", "Tasks", Icons.Default.Task)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    companion object {
        val bottomNavItems = listOf(Home, Library, Research, Chat, KnowledgeGraph)
    }
}

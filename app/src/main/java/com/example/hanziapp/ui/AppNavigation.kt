package com.example.hanziapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.hanziapp.data.CharacterRepository
import com.example.hanziapp.tts.TtsManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(repo: CharacterRepository, tts: TtsManager) {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(nav) { scope.launch { drawerState.close() } }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(titleForRoute(nav.currentBackStackEntryAsState().value?.destination?.route)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Open menu")
                        }
                    }
                )
            }
        ) { pad ->
            NavHost(
                navController = nav,
                startDestination = "dashboard",
                modifier = Modifier.padding(pad)
            ) {
                composable("dashboard") { DashboardScreen(repo, nav) }
                composable("library") { LibraryScreen(repo, tts) }
                composable("practice") { PracticeSessionScreen(repo, tts) { nav.popBackStack() } }
                composable("random") { RandomTestScreen(repo, tts) }
                composable("sentences") { SentencesScreen() }
                composable("conversation") { ConversationScreen() }
                composable("settings") { SettingsScreen() }
            }
        }
    }
}

private fun titleForRoute(route: String?): String = when (route) {
    "dashboard" -> "Hanzi Learner"
    "library" -> "Library"
    "practice" -> "Practice"
    "random" -> "Random Test"
    "sentences" -> "Sentence Builder"
    "conversation" -> "Conversation"
    "settings" -> "Settings"
    else -> "Hanzi Learner"
}
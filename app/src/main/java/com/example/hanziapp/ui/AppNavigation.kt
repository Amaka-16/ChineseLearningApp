package com.example.hanziapp.ui

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.hanziapp.data.CharacterRepository
import com.example.hanziapp.tts.TtsManager

@Composable
fun AppNavigation(repo: CharacterRepository, tts: TtsManager) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(repo, nav) }
        composable("review") { ReviewScreen(repo, tts) { nav.popBackStack() } }
        composable("learn") { ReviewScreen(repo, tts) { nav.popBackStack() } }
    }
}
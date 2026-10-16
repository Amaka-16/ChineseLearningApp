package com.example.hanziapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.hanziapp.data.CharacterRepository
import com.example.hanziapp.tts.TtsManager

@Composable
fun RandomTestScreen(repo: CharacterRepository, tts: TtsManager) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Random Test — coming in Stage 4", style = MaterialTheme.typography.titleMedium)
    }
}
package com.example.hanziapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hanziapp.data.Character
import com.example.hanziapp.tts.TtsManager

@Composable
fun CharacterDetailScreen(character: Character, tts: TtsManager) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(character.hanzi, fontSize = 120.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(character.pinyin, fontSize = 28.sp)
        Text(character.definition, fontSize = 18.sp)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { tts.speak(character.hanzi) }) { Text("🔊 Pronounce") }
        Spacer(Modifier.height(24.dp))
        WritingCanvas(Modifier.fillMaxWidth())
    }
}
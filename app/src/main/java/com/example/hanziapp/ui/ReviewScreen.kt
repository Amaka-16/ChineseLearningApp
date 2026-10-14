package com.example.hanziapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hanziapp.data.CardState
import com.example.hanziapp.data.Character
import com.example.hanziapp.data.CharacterRepository
import com.example.hanziapp.data.UserProgress
import com.example.hanziapp.srs.FsrsScheduler
import com.example.hanziapp.srs.Rating
import com.example.hanziapp.tts.TtsManager
import kotlinx.coroutines.launch

@Composable
fun ReviewScreen(repo: CharacterRepository, tts: TtsManager, onDone: () -> Unit) {
    val scope = rememberCoroutineScope()
    var queue by remember { mutableStateOf<List<Character>>(emptyList()) }
    var index by remember { mutableIntStateOf(0) }
    var revealed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val due = repo.dueReviewQueue()
        queue = if (due.isNotEmpty()) due else repo.newQueue()
    }

    if (index >= queue.size) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Session complete!", fontSize = 24.sp)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onDone) { Text("Back") }
        }
        return
    }

    val character = queue[index]

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LinearProgressIndicator(
            progress = { (index + 1f) / queue.size },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(32.dp))
        Text(character.hanzi, fontSize = 140.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(24.dp))
        Button(onClick = { tts.speak(character.hanzi) }) { Text("🔊") }

        Spacer(Modifier.height(24.dp))
        if (!revealed) {
            Button(onClick = { revealed = true }) { Text("Show Answer") }
        } else {
            Text(character.pinyin, fontSize = 26.sp)
            Text(character.definition, fontSize = 18.sp)
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Rating.AGAIN to "Again",
                    Rating.HARD to "Hard",
                    Rating.GOOD to "Good",
                    Rating.EASY to "Easy"
                ).forEach { (r, label) ->
                    Button(onClick = {
                        scope.launch {
                            val existing = repo.getProgress(character.hanzi)
                                ?: UserProgress(hanzi = character.hanzi, state = CardState.NEW)
                            val updated = FsrsScheduler.schedule(existing, r)
                            repo.saveProgress(updated)
                            revealed = false
                            index++
                        }
                    }) { Text(label) }
                }
            }
        }
    }
}
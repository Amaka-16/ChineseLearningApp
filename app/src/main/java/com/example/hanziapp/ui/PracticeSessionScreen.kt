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
import com.example.hanziapp.tts.TtsManager
import kotlinx.coroutines.launch

@Composable
fun PracticeSessionScreen(
    repo: CharacterRepository,
    tts: TtsManager,
    onDone: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var queue by remember { mutableStateOf<List<Character>>(emptyList()) }
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<CardState?>(null) }
    var revealed by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        queue = repo.buildMixedQueue(20)
        loading = false
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    if (queue.isEmpty()) {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No cards to practice yet.", fontSize = 20.sp)
            Spacer(Modifier.height(8.dp))
            Text("Add characters in the Library first.", fontSize = 14.sp)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onDone) { Text("Back") }
        }
        return
    }

    if (index >= queue.size) {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Session complete!", fontSize = 24.sp)
            Spacer(Modifier.height(8.dp))
            Text("${queue.size} cards reviewed", fontSize = 14.sp)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onDone) { Text("Back") }
        }
        return
    }

    val character = queue[index]

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress row
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onDone) { Text("End Session") }
            Text("${index + 1} / ${queue.size}", fontSize = 14.sp)
        }
        LinearProgressIndicator(
            progress = { (index + 1f) / queue.size },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        // Character — always visible
        Text(character.hanzi, fontSize = 120.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(16.dp))

        // Sound button — always visible
        Button(onClick = { tts.speak(character.hanzi) }) { Text("🔊 Listen") }

        Spacer(Modifier.height(24.dp))

        if (!revealed) {
            // Hidden state: only Show Answer button
            Button(
                onClick = { revealed = true },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Show Answer") }
        } else {
            // Revealed state: pinyin, meaning, verdicts, canvas, next
            Text(character.pinyin, fontSize = 28.sp)

            Spacer(Modifier.height(8.dp))

            Text(character.definition, fontSize = 18.sp)

            Spacer(Modifier.height(24.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VerdictButton(
                    label = "Known",
                    selected = selected == CardState.KNOWN,
                    modifier = Modifier.weight(1f)
                ) { selected = CardState.KNOWN }

                VerdictButton(
                    label = "Learning",
                    selected = selected == CardState.LEARNING,
                    modifier = Modifier.weight(1f)
                ) { selected = CardState.LEARNING }

                VerdictButton(
                    label = "Unknown",
                    selected = selected == CardState.UNKNOWN,
                    modifier = Modifier.weight(1f)
                ) { selected = CardState.UNKNOWN }
            }

            Spacer(Modifier.height(16.dp))

            WritingCanvas(Modifier.fillMaxWidth())

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val verdict = selected ?: CardState.LEARNING
                    scope.launch {
                        val existing = repo.getProgress(character.hanzi)
                            ?: UserProgress(hanzi = character.hanzi, state = CardState.NEW)
                        val updated = applyVerdict(existing, verdict)
                        repo.saveProgress(updated)
                        selected = null
                        revealed = false
                        index++
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selected != null
            ) { Text("Next") }
        }
    }
}

@Composable
private fun VerdictButton(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    if (selected) {
        Button(onClick = onClick, modifier = modifier) { Text(label) }
    } else {
        OutlinedButton(onClick = onClick, modifier = modifier) { Text(label) }
    }
}

private fun applyVerdict(p: UserProgress, verdict: CardState): UserProgress {
    val now = System.currentTimeMillis()
    val dayMs = 86_400_000L
    val intervalDays = when (verdict) {
        CardState.KNOWN -> maxOf(7, (p.stability * 4).toLong())
        CardState.LEARNING -> 1L
        CardState.UNKNOWN -> 0L
        CardState.NEW -> 0L
    }
    return p.copy(
        state = verdict,
        lastReview = now,
        dueDate = now + intervalDays * dayMs,
        reps = p.reps + 1,
        stability = when (verdict) {
            CardState.KNOWN -> p.stability + 2.0
            CardState.LEARNING -> p.stability + 0.5
            else -> 0.0
        }
    )
}
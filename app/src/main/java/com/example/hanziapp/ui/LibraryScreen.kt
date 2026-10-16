package com.example.hanziapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hanziapp.data.Character
import com.example.hanziapp.data.CharacterRepository
import com.example.hanziapp.tts.TtsManager
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(repo: CharacterRepository, tts: TtsManager) {
    var query by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<Character?>(null) }
    val scope = rememberCoroutineScope()

    val results by if (query.isBlank()) {
        repo.allCharacters.collectAsState(initial = emptyList())
    } else {
        repo.searchFlow(query).collectAsState(initial = emptyList())
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search hanzi, pinyin, meaning") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = { showAddDialog = true }) { Text("Add") }
        }
        Spacer(Modifier.height(8.dp))
        Text("${results.size} entries", fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))

        LazyColumn {
            items(results, key = { it.id }) { c ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { selected = c }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(c.hanzi, fontSize = 24.sp)
                        if (c.traditional.isNotBlank() && c.traditional != c.hanzi) {
                            Text("( ${c.traditional} )", fontSize = 12.sp)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(c.pinyin)
                        Text(c.definition, fontSize = 12.sp)
                        if (c.partOfSpeech.isNotBlank()) {
                            Text(c.partOfSpeech, fontSize = 10.sp)
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }

    if (showAddDialog) {
        AddCharacterDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newChar ->
                scope.launch {
                    repo.addCharacter(newChar)
                    showAddDialog = false
                }
            }
        )
    }

    selected?.let { c ->
        FlashcardDialog(character = c, tts = tts, onDismiss = { selected = null })
    }
}

@Composable
fun FlashcardDialog(character: Character, tts: TtsManager, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Character") },
        text = {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(character.hanzi, fontSize = 96.sp, fontWeight = FontWeight.Bold)
                if (character.traditional.isNotBlank() && character.traditional != character.hanzi) {
                    Text("( ${character.traditional} )", fontSize = 18.sp)
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { tts.speak(character.hanzi) }) {
                        Text("🔊", fontSize = 24.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(character.pinyin, fontSize = 22.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(character.definition, fontSize = 16.sp)
                if (character.partOfSpeech.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(character.partOfSpeech, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun AddCharacterDialog(
    onDismiss: () -> Unit,
    onSave: (Character) -> Unit
) {
    var hanzi by remember { mutableStateOf("") }
    var traditional by remember { mutableStateOf("") }
    var pinyin by remember { mutableStateOf("") }
    var definition by remember { mutableStateOf("") }
    var pos by remember { mutableStateOf("") }
    var strokes by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add a character") },
        text = {
            Column {
                OutlinedTextField(value = hanzi, onValueChange = { hanzi = it }, label = { Text("Hanzi (simplified)") })
                OutlinedTextField(value = traditional, onValueChange = { traditional = it }, label = { Text("Traditional (optional)") })
                OutlinedTextField(value = pinyin, onValueChange = { pinyin = it }, label = { Text("Pinyin") })
                OutlinedTextField(value = definition, onValueChange = { definition = it }, label = { Text("Meaning") })
                OutlinedTextField(value = pos, onValueChange = { pos = it }, label = { Text("Part of speech (optional)") })
                OutlinedTextField(value = strokes, onValueChange = { strokes = it }, label = { Text("Strokes") })
            }
        },
        confirmButton = {
            Button(
                enabled = hanzi.isNotBlank() && pinyin.isNotBlank() && definition.isNotBlank(),
                onClick = {
                    onSave(
                        Character(
                            hanzi = hanzi.trim(),
                            traditional = traditional.trim(),
                            pinyin = pinyin.trim(),
                            definition = definition.trim(),
                            partOfSpeech = pos.trim(),
                            category = "user_added",
                            strokeCount = strokes.toIntOrNull() ?: 0,
                            hskLevel = 0,
                            isWord = hanzi.trim().length > 1
                        )
                    )
                }
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
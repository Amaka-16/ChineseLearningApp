package com.example.hanziapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hanziapp.data.CardState
import com.example.hanziapp.data.CharacterRepository

@Composable
fun DashboardScreen(repo: CharacterRepository, nav: NavController) {
    val progress by repo.allProgress.collectAsState(initial = emptyList())
    val characters by repo.allCharacters.collectAsState(initial = emptyList())

    val known = progress.count { it.state == CardState.KNOWN }
    val learning = progress.count { it.state == CardState.LEARNING }
    val unknown = progress.count { it.state == CardState.UNKNOWN }
    val unseen = characters.size - progress.size

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BucketCard("Known",    known,    Modifier.weight(1f))
            BucketCard("Learning", learning, Modifier.weight(1f))
            BucketCard("Unknown",  unknown,  Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Text("Unseen: $unseen", fontSize = 14.sp)

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { nav.navigate("practice") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Practice") }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { nav.navigate("library") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Library") }

        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = { nav.navigate("random") },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Random Test") }

        Spacer(Modifier.height(24.dp))
        Text("All Characters", fontSize = 20.sp)
        Spacer(Modifier.height(8.dp))

        LazyColumn {
            items(characters) { c ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Row(
                        Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(c.hanzi, fontSize = 28.sp)
                        Column {
                            Text(c.pinyin)
                            Text(c.definition, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BucketCard(label: String, count: Int, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(
            Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(count.toString(), fontSize = 28.sp)
            Text(label, fontSize = 12.sp)
        }
    }
}
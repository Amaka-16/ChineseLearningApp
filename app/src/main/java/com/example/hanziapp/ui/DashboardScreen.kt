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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(repo: CharacterRepository, nav: NavController) {
    val progress by repo.allProgress.collectAsState(initial = emptyList())
    val characters by repo.allCharacters.collectAsState(initial = emptyList())

    val learnt = progress.count { it.state == CardState.LEARNT }
    val learning = progress.count { it.state == CardState.LEARNING }
    val newCount = characters.size - progress.size

    Scaffold(topBar = { TopAppBar(title = { Text("Hanzi Learner") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatCard("Learnt", learnt)
                StatCard("Learning", learning)
                StatCard("New", newCount)
            }
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { nav.navigate("review") },
                modifier = Modifier.fillMaxWidth()
            ) {
                val dueNow = progress.count {
                    it.dueDate <= System.currentTimeMillis() && it.state != CardState.NEW
                }
                Text("Start Review ($dueNow)")
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { nav.navigate("learn") },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Learn New") }

            Spacer(Modifier.height(24.dp))
            Text("All Characters", fontSize = 20.sp)

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
}

@Composable
fun StatCard(label: String, count: Int) {
    Card { Column(Modifier.padding(16.dp)) {
        Text(count.toString(), fontSize = 32.sp)
        Text(label)
    } }
}
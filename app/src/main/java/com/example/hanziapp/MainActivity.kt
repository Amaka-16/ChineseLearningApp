package com.example.hanziapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.lifecycleScope
import com.example.hanziapp.data.AppDatabase
import com.example.hanziapp.data.CharacterRepository
import com.example.hanziapp.tts.TtsManager
import com.example.hanziapp.ui.AppNavigation
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var tts: TtsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = AppDatabase.get(this).characterDao()
        val repo = CharacterRepository(dao, this)
        tts = TtsManager(this)

        lifecycleScope.launch { repo.seedIfEmpty() }

        setContent {
            MaterialTheme {
                Surface { AppNavigation(repo, tts) }
            }
        }
    }

    override fun onDestroy() {
        tts.shutdown()
        super.onDestroy()
    }
}
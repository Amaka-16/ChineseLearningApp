package com.example.hanziapp.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CharacterRepository(
    private val dao: CharacterDao,
    private val context: Context
) {
    val allCharacters = dao.allCharacters()
    val allProgress = dao.allProgress()

    suspend fun seedIfEmpty() {
        val json = context.assets.open("cedict_sample.json")
            .bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Character>>() {}.type
        val chars: List<Character> = Gson().fromJson(json, type)
        dao.insertCharacters(chars)
    }

    suspend fun getCharacter(h: String) = dao.getCharacter(h)
    suspend fun getProgress(h: String) = dao.getProgress(h)
    suspend fun saveProgress(p: UserProgress) = dao.upsertProgress(p)
    suspend fun dueReviewQueue(limit: Int = 20) =
        dao.getDueCharacters(System.currentTimeMillis(), limit)
    suspend fun newQueue(limit: Int = 10) = dao.getNewCharacters(limit)
}
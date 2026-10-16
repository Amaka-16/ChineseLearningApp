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

    fun searchFlow(query: String) = dao.searchCharacters(query)

    suspend fun getCharacter(h: String) = dao.getCharacter(h)
    suspend fun getProgress(h: String) = dao.getProgress(h)
    suspend fun saveProgress(p: UserProgress) = dao.upsertProgress(p)

    suspend fun addCharacter(c: Character) {
        dao.insertCharacter(c)
        dao.upsertProgress(UserProgress(hanzi = c.hanzi, state = CardState.NEW))
    }

    suspend fun buildMixedQueue(total: Int = 20): List<Character> {
        val knownTarget = (total * 0.4).toInt()
        val learningTarget = (total * 0.4).toInt()
        val unknownTarget = total - knownTarget - learningTarget

        val known = dao.getRandomByState("KNOWN", knownTarget).toMutableList()
        val learning = dao.getRandomByState("LEARNING", learningTarget).toMutableList()
        val unknown = dao.getRandomByState("UNKNOWN", unknownTarget).toMutableList()

        val combined = (known + learning + unknown).toMutableList()
        if (combined.size < total) {
            val need = total - combined.size
            combined.addAll(dao.getRandomNew(need))
        }
        return combined.shuffled().take(total)
    }
}
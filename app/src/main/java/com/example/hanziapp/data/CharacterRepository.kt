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
    val allCategories = dao.allCategories()

    suspend fun seedIfEmpty() {
        val files = listOf("cedict_sample.json", "my_characters.json")
        for (file in files) {
            try {
                val json = context.assets.open(file).bufferedReader().use { it.readText() }
                val type = object : TypeToken<List<Character>>() {}.type
                val chars: List<Character> = Gson().fromJson(json, type)
                dao.insertCharacters(chars)
            } catch (e: Exception) {
                // File doesn't exist yet — skip silently
            }
        }
    }

    fun searchFlow(query: String) = dao.searchCharacters(query)
    fun categoryFlow(category: String) = dao.charactersByCategory(category)

    suspend fun getCharacter(id: Long) = dao.getCharacter(id)
    suspend fun getProgress(id: Long) = dao.getProgress(id)
    suspend fun saveProgress(p: UserProgress) = dao.upsertProgress(p)

    suspend fun addCharacter(c: Character) {
        dao.insertCharacter(c)
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
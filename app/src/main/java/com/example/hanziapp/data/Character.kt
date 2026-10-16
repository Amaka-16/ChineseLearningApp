package com.example.hanziapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class Character(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val hanzi: String,
    val traditional: String = "",
    val pinyin: String,
    val definition: String,
    val partOfSpeech: String = "",
    val category: String = "",
    val strokeCount: Int = 0,
    val hskLevel: Int = 0,
    val isWord: Boolean = false
)

enum class CardState { NEW, LEARNING, KNOWN, UNKNOWN }

@Entity(tableName = "progress")
data class UserProgress(
    @PrimaryKey val characterId: Long,
    val state: CardState = CardState.NEW,
    val stability: Double = 0.0,
    val difficulty: Double = 0.0,
    val dueDate: Long = System.currentTimeMillis(),
    val lastReview: Long = 0L,
    val reps: Int = 0,
    val lapses: Int = 0
)
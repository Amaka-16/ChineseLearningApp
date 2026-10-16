package com.example.hanziapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class Character(
    @PrimaryKey val hanzi: String,
    val pinyin: String,
    val definition: String,
    val strokeCount: Int,
    val hskLevel: Int = 0,
    val isWord: Boolean = false
)

enum class CardState { NEW, LEARNING, KNOWN, UNKNOWN }

@Entity(tableName = "progress")
data class UserProgress(
    @PrimaryKey val hanzi: String,
    val state: CardState = CardState.NEW,
    val stability: Double = 0.0,
    val difficulty: Double = 0.0,
    val dueDate: Long = System.currentTimeMillis(),
    val lastReview: Long = 0L,
    val reps: Int = 0,
    val lapses: Int = 0
)
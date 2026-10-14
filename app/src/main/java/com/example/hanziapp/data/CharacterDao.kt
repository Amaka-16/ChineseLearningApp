package com.example.hanziapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Query("SELECT * FROM characters")
    fun allCharacters(): Flow<List<Character>>

    @Query("SELECT * FROM characters WHERE hanzi = :hanzi LIMIT 1")
    suspend fun getCharacter(hanzi: String): Character?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCharacters(chars: List<Character>)

    @Query("SELECT * FROM progress")
    fun allProgress(): Flow<List<UserProgress>>

    @Query("SELECT * FROM progress WHERE hanzi = :hanzi LIMIT 1")
    suspend fun getProgress(hanzi: String): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: UserProgress)

    @Query("""
        SELECT c.* FROM characters c
        INNER JOIN progress p ON c.hanzi = p.hanzi
        WHERE p.state != 'NEW' AND p.dueDate <= :now
        ORDER BY p.dueDate ASC LIMIT :limit
    """)
    suspend fun getDueCharacters(now: Long, limit: Int): List<Character>

    @Query("""
        SELECT c.* FROM characters c
        LEFT JOIN progress p ON c.hanzi = p.hanzi
        WHERE p.hanzi IS NULL
        LIMIT :limit
    """)
    suspend fun getNewCharacters(limit: Int): List<Character>
}
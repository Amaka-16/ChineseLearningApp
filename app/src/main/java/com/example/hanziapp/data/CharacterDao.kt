package com.example.hanziapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Query("SELECT * FROM characters ORDER BY hanzi ASC")
    fun allCharacters(): Flow<List<Character>>

    @Query("SELECT * FROM characters WHERE hanzi = :hanzi LIMIT 1")
    suspend fun getCharacter(hanzi: String): Character?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCharacters(chars: List<Character>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: Character)

    @Query("SELECT * FROM characters WHERE hanzi LIKE '%' || :q || '%' OR pinyin LIKE '%' || :q || '%' OR definition LIKE '%' || :q || '%' ORDER BY hanzi ASC LIMIT 200")
    fun searchCharacters(q: String): Flow<List<Character>>

    @Query("SELECT * FROM progress")
    fun allProgress(): Flow<List<UserProgress>>

    @Query("SELECT * FROM progress WHERE hanzi = :hanzi LIMIT 1")
    suspend fun getProgress(hanzi: String): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: UserProgress)

    @Query("""
        SELECT c.* FROM characters c
        INNER JOIN progress p ON c.hanzi = p.hanzi
        WHERE p.state = :state
        ORDER BY RANDOM() LIMIT :limit
    """)
    suspend fun getRandomByState(state: String, limit: Int): List<Character>

    @Query("""
        SELECT c.* FROM characters c
        LEFT JOIN progress p ON c.hanzi = p.hanzi
        WHERE p.hanzi IS NULL OR p.state = 'NEW'
        ORDER BY RANDOM() LIMIT :limit
    """)
    suspend fun getRandomNew(limit: Int): List<Character>
}
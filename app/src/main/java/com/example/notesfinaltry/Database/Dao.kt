package com.example.notesfinaltry.Database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getAll(): Flow<List<Note>>

    @Insert
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE FROM note")
    suspend fun deleteAllNotes()

    @Update
    suspend fun editNote (note: Note)


}

package com.example.notesfinaltry.Database

import kotlinx.coroutines.flow.Flow


interface NotesRepository {

    fun getNote(): Flow<List<Note>>

    suspend fun insertNote(notes: Note)

    suspend fun deleteNote(notes: Note)

    suspend fun deleteAll()

    suspend fun editNote(notes: Note)


}

class NotesRepositoryImp (private val notesDao: NoteDao) : NotesRepository  {

    override fun getNote():Flow<List<Note>> {
        return notesDao.getAll()
    }

    override suspend fun insertNote(notes: Note) {
        return notesDao.insert(notes)
    }

    override suspend fun deleteNote(notes: Note) {
        return notesDao.delete(notes)
    }

    override suspend fun deleteAll() {
        return notesDao.deleteAllNotes()
    }

    override suspend fun editNote(notes: Note) {
        return notesDao.editNote(notes)
    }



}

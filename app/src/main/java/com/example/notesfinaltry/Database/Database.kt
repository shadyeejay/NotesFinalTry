package com.example.notesfinaltry.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.notesfinaltry.Composables.ArrayListConverter


@Database(entities = [Note::class], version = 1)
@TypeConverters(ArrayListConverter::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    companion object {
        private var INSTANCE: NoteDatabase? = null
        fun getDatabase(context: Context): NoteDatabase {
           return INSTANCE ?: synchronized(this) {
               val instance = Room.databaseBuilder(
                   context.applicationContext,
                   NoteDatabase::class.java,
                   "notes_db"
               ).build()
               INSTANCE = instance
               instance
           }
        }
    }
}
package com.example.notesfinaltry

import android.app.Application
import com.example.notesfinaltry.Data.SharedPref
import com.example.notesfinaltry.Database.*

class NotesApplication: Application() {
    lateinit var notesRepositoryImp: NotesRepositoryImp
    lateinit var sharedPref: SharedPref
    override fun onCreate() {
        super.onCreate()
        notesRepositoryImp = NotesRepositoryImp(NoteDao_Impl(NoteDatabase.getDatabase(this)))
        sharedPref = SharedPref(this)
    }
}
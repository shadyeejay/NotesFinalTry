package com.example.notesfinaltry.Data

import androidx.compose.runtime.mutableStateListOf
import com.example.notesfinaltry.Composables.CheckList
import com.example.notesfinaltry.Database.Note

data class NotesUiState(
    val uid: Int = 0,
    val header: String = "",
    val note: String = "",
    val searchQuery: String = "",
    val checklistEntry: String = "",
    val checklistEntryNumber: Int? = null,
    val showContent: Boolean = false,
    val sheetState: Boolean = false,
    val toggle: Boolean = false,
    val canSave: Boolean = false,
    val allNotes: List<Note> = mutableStateListOf(),
    val toDoList: List<CheckList> = mutableStateListOf()
)

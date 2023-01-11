package com.example.notesfinaltry.Data
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.notesfinaltry.Composables.CheckList
import com.example.notesfinaltry.Composables.CheckString
import com.example.notesfinaltry.Database.*
import com.example.notesfinaltry.NotesApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*


class NotesViewModel (
    private val notesRepositoryImp: NotesRepositoryImp,
    private val sharedPref: SharedPref,
): ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    val temporaryDeleteMemory = mutableStateListOf<Note>()
    val temporaryCheckList = mutableStateListOf<CheckList>()

    private val checkString = CheckString()
    @RequiresApi(Build.VERSION_CODES.O)
    private val getDate = DateUtils().getTime()

    init {
        updateState()
        updateList()
    }

    fun deleteTally(note: Note) {
        if(temporaryDeleteMemory.contains(note))
            temporaryDeleteMemory.remove(note) else temporaryDeleteMemory.add(note)
    }

    fun finderNote (string: String): List<Note> {
        val list = mutableStateListOf<Note>()
        viewModelScope.launch {
            for (notes in _uiState.value.allNotes) {
                if(notes.checkList == null) {
                    if(notes.note?.lowercase()?.contains(string.lowercase()) == true
                        || notes.header?.lowercase()?.contains(string.lowercase()) == true)
                    { list.add(notes) }
                }
            }
        }
        return if(_uiState.value.toggle) list else list.reversed()
    }

    fun finderChecklist (string: String): List<Note> {
        val list = mutableStateListOf<Note>()
        viewModelScope.launch {
            for (notes in _uiState.value.allNotes) {
                notes.checkList?.let {
                    for (checklistItems in notes.checkList) {
                        if(checklistItems.note.lowercase().contains(string.lowercase())
                            || notes.header?.lowercase()?.contains(string.lowercase()) == true) {
                            list.add(notes)
                            break
                        }
                    }
                }
            }
        }
        return if(_uiState.value.toggle) list else list.reversed()
    }

    fun updateStates(boolean: Boolean) {
        viewModelScope.launch { sharedPref.saveSort(boolean) }
    }

    private fun updateState() {
        viewModelScope.launch {
            sharedPref.getSort.collect{
                 _uiState.update {  currentState ->
                     currentState.copy(
                         toggle = it
                     )
                 }
            }
        }
    }

    fun clearValues() {
        _uiState.update { currentState ->
            currentState.copy(
                uid = 0,
                header = "",
                note = "",
                searchQuery = ""
            )
        }
        temporaryCheckList.clear()
    }

    fun clearSearchQuery() {
        _uiState.update { currentState ->
             currentState.copy(
                searchQuery = ""
            )
        }
    }

    private fun updateList() {
        viewModelScope.launch {
            notesRepositoryImp.getNote().collect {
                _uiState.update { update -> update.copy(allNotes = it) }
            }
        }
    }

    private fun addNote(notes: Note) {
        if(_uiState.value.header.isNotEmpty() || _uiState.value.note.isNotEmpty()) {
            viewModelScope.launch {
                notesRepositoryImp.insertNote(notes)
            }
        }
    }

    fun editNote(note: Note) {
        viewModelScope.launch {
            notesRepositoryImp.editNote(note)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addChecklist(note: Note) {
        if(_uiState.value.header.isNotEmpty() || temporaryCheckList.isNotEmpty()) {
            viewModelScope.launch {
                notesRepositoryImp.insertNote(note)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editOrAddNote(){
        if (uiState.value.uid == 0) {
            addNote(Note(null, checkString.checkString(uiState.value.header),checkString.checkString(uiState.value.note),getDate,null))
        } else editNote(Note(uiState.value.uid, checkString.checkString(uiState.value.header), checkString.checkString(uiState.value.note), getDate,null))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun editOrAddChecklist(){
        if (uiState.value.uid == 0) {
            addChecklist(Note(null, checkString.checkString(uiState.value.header), null,getDate,ArrayList(temporaryCheckList)))
        } else editNote(Note(uiState.value.uid, checkString.checkString(uiState.value.header), null, getDate,ArrayList(temporaryCheckList)))
    }


    fun clearAll() {
        viewModelScope.launch {
            notesRepositoryImp.deleteAll()
        }
    }

    fun delete(note: Note) {
        viewModelScope.launch {
            notesRepositoryImp.deleteNote(note)
        }
    }


    fun deleteSelected() {
        viewModelScope.launch {
            for (notes in temporaryDeleteMemory) {
                notesRepositoryImp.deleteNote(notes)
            }
            delay(10)
            temporaryDeleteMemory.clear()
        }
    }

    fun uid(uid: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                uid = uid
            )
        }
    }

    fun header(header: String) {
        _uiState.update { currentState ->
            currentState.copy(
                header = header
            )
        }
    }

    fun searchQuery(query:String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query
            )
        }
    }

    fun note(note: String) {
        _uiState.update { currentState ->
            currentState.copy(
                note = note
            )
        }
    }

    fun toDoList(note: List<CheckList>) {
        note.forEach {
            temporaryCheckList.add(it)
        }
    }

    fun sheetState(collapse: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                sheetState = collapse
            )
        }
    }

    fun showContent(show: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                showContent = show
            )
        }
    }

    fun checklistEntry(entry: String) {
        _uiState.update { currentState ->
            currentState.copy(
                checklistEntry = entry
            )
        }
    }

    fun checkSavableChecklist():Boolean{
        return uiState.value.header.isNotEmpty() || temporaryCheckList.isNotEmpty()
    }

    fun checkSavableNotes(): Boolean{
        return uiState.value.header.isNotEmpty() || uiState.value.note.isNotEmpty()
    }

    fun addOrEditEntry() {
        when {
            uiState.value.checklistEntry.isNotEmpty() && uiState.value.checklistEntryNumber != null -> {
                temporaryCheckList[uiState.value.checklistEntryNumber!!] = CheckList(uiState.value.checklistEntry,0)
                _uiState.update { currentState ->
                    currentState.copy(
                        checklistEntry = "",
                        checklistEntryNumber = null
                    )
                }
            }
            uiState.value.checklistEntry.isNotEmpty() -> {
                temporaryCheckList.add(CheckList(uiState.value.checklistEntry,0))
                _uiState.update { currentState ->
                    currentState.copy(
                        checklistEntry = ""
                    )
                }
            }
        }
    }

    fun editChecklistEntry(entry: String, entryLocation: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                checklistEntry = entry,
                checklistEntryNumber = entryLocation
            )
        }
    }

    fun deleteCurrent() {
        delete(Note(uiState.value.uid,null,null,null,null))
    }

    fun selectAllNotes() {
        if(temporaryDeleteMemory.containsAll(_uiState.value.allNotes)) {
            temporaryDeleteMemory.clear()
        } else temporaryDeleteMemory += _uiState.value.allNotes
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val notesRepositoryImp = (this[APPLICATION_KEY] as NotesApplication).notesRepositoryImp
                val sharedPref = (this[APPLICATION_KEY] as NotesApplication).sharedPref
                NotesViewModel(
                    notesRepositoryImp = notesRepositoryImp,
                    sharedPref = sharedPref,
                )
            }
        }
    }
}



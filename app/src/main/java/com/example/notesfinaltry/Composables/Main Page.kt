package com.example.notesfinaltry.Composables
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavController
import com.example.notesfinaltry.Data.NotesViewModel
import com.example.notesfinaltry.Data.PageNav

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainScreen(navController: NavController, noteView: NotesViewModel) {

    val coScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val myUiState by noteView.uiState.collectAsState()
    val allNotes = noteView.finderNote(myUiState.searchQuery)
    val allCheckList = noteView.finderChecklist(myUiState.searchQuery)
    val state = remember { MutableTransitionState(false).apply { targetState = true } }
    var disable by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }

    AnimatedVisibility(visibleState = state,
        enter = slideInHorizontally(
            animationSpec = tween(400,0, easing = FastOutSlowInEasing),
            initialOffsetX = { fullWidth -> -fullWidth }),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(400,0, easing = FastOutSlowInEasing)))
    {
        Column(modifier = Modifier.fillMaxSize()) {
            BottomTools(
                searchQuery = myUiState.searchQuery,
                searchQueryIt = { noteView.searchQuery(it) },
                deleteAll = { noteView.selectAllNotes() },
                deleteSelected = { expanded = true },
                navController = navController ,
                noteView = noteView,
                allNotes = allNotes,
                allChecklist = allCheckList,
                focusManager = focusManager,
                keyboardController = keyboardController!!,
                focusRequester = focusRequester,
                coScope = coScope,
                addNote = {
                    if(disable) {
                        disable = false
                        navController.navigate(route = PageNav.AddNote.name)
                    }
                },
                addCheckList = {
                    if(disable) {
                        disable = false
                        noteView.clearValues()
                        navController.navigate(route = PageNav.AddChecklist.name)
                    }
                }
            )
            ConfirmDelete(
                popUp = expanded,
                confirmDelete = { noteView.deleteSelected() },
                cancel = { expanded = false }
            )
        }
    }
}







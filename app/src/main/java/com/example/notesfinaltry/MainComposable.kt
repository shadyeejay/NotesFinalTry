package com.example.notesfinaltry
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.notesfinaltry.Data.NotesViewModel
import com.example.notesfinaltry.Data.PageNav
import com.example.notesfinaltry.Composables.LoadingScreen
import com.example.notesfinaltry.Composables.NoteComposer
import com.example.notesfinaltry.Composables.MainScreen
import com.example.notesfinaltry.Composables.test

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MainComposable(
    notesViewModel: NotesViewModel = viewModel(factory = NotesViewModel.Factory),
) {
    val navController = rememberNavController()
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val focusRequester = FocusRequester()
    val listState = rememberLazyListState()
    val scrollState = rememberScrollState()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route?.let {}
    val currentScreenOne = backStackEntry?.destination?.route

    NavHost(
        navController = navController,
        startDestination = PageNav.LoadingScreen.name,
    ) {
        composable(route = PageNav.LoadingScreen.name,) {
            LoadingScreen(navController = navController)
        }
        composable(route = PageNav.AllNotes.name) {
            BackHandler(true) {}
            MainScreen(noteView = notesViewModel, navController = navController)
        }
        composable(route = PageNav.AddNote.name) {
            if (keyboardController != null) {
                NoteComposer(
                    notesView = notesViewModel,
                    navController = navController,
                    focusRequester = focusRequester,
                    scrollState = scrollState,
                    keyboardController = keyboardController,
                    coroutineScope = scope
                )
            }
        }
        composable(route = PageNav.AddChecklist.name) {
            if (keyboardController != null) {
                test(
                    notesView = notesViewModel,
                    navController = navController,
                    keyboardController = keyboardController,
                    coroutineScope = scope,
                    focusRequester = focusRequester,
                    listState = listState
                )
            }
        }
    }

}


package com.example.notesfinaltry.Composables
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notesfinaltry.Data.NotesViewModel
import com.example.notesfinaltry.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NoteComposer(
    navController: NavController,
    notesView: NotesViewModel,
    focusRequester: FocusRequester,
    scrollState: ScrollState,
    keyboardController: SoftwareKeyboardController,
    coroutineScope: CoroutineScope

) {

    val myUiState by notesView.uiState.collectAsState()
    val state = remember { MutableTransitionState(false).apply { targetState = true } }
    val returnAndSave = {
        coroutineScope.launch {
            keyboardController.hide()
            notesView.editOrAddNote()
            delay(50)
            navController.navigateUp()
        }
    }

    AnimatedVisibility(
        visibleState = state,
        enter = slideInHorizontally(
            animationSpec = tween(200,200, easing = FastOutSlowInEasing),
            initialOffsetX = { fullWidth -> fullWidth }),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(200,200, easing = FastOutSlowInEasing)
        ),
    ) {
        Scaffold(
            topBar = {
                TopNavigation(
                    backButton = { returnAndSave() },
                    deleteNote = {
                        notesView.deleteCurrent()
                        navController.navigateUp()
                    },
                    saveNote = { returnAndSave() },
                    noteView = notesView,
                    canSave = notesView.checkSavableNotes()
                )
            }
        ) { padding ->
            Column (
                modifier = Modifier
                    .background(Color(0xFF1F3548))
                    .fillMaxSize()
                    .padding(padding),
            ) {
                TextField(
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        textColor = Color.White,
                        placeholderColor = Color.LightGray
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.title),
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp
                        )
                    },
                    value = myUiState.header,
                    onValueChange = { notesView.header(it) },
                    keyboardOptions = KeyboardOptions(
                        KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next
                    )
                )

                Divider(
                    modifier = Modifier.padding(5.dp),
                    thickness = 2.dp,
                    color = Color.LightGray
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState, reverseScrolling = true)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalArrangement = Arrangement.Top,

                    ) {
                    TextField(
                        value = myUiState.note,
                        onValueChange = { notesView.note(it) },
                        keyboardOptions = KeyboardOptions(
                            KeyboardCapitalization.Sentences,
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            textColor = Color.White,
                            placeholderColor = Color.LightGray
                        ),
                        placeholder = { Text(stringResource(R.string.note)) },
                        modifier = Modifier
                            .fillMaxSize()
                            .focusRequester(focusRequester)
                    )
                }
            }
            BackHandler(
                onBack = {
                    coroutineScope.launch {
                        notesView.editOrAddNote()
                        delay(50)
                        navController.navigateUp()
                    }
                }
            )
        }
    }
}



package com.example.notesfinaltry.Composables

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.notesfinaltry.Data.NotesViewModel
import com.example.notesfinaltry.Database.Note
import com.example.notesfinaltry.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun test(
    notesView: NotesViewModel,
    navController: NavController,
    keyboardController: SoftwareKeyboardController,
    coroutineScope: CoroutineScope,
    focusRequester: FocusRequester,
    listState: LazyListState
) {

    val myUiState by notesView.uiState.collectAsState()
    val transitionState = remember { MutableTransitionState(false).apply { targetState = true } }
    val returnAndSave = {
        coroutineScope.launch {
            keyboardController.hide()
            notesView.editOrAddChecklist()
            delay(50)
            navController.navigateUp()
        }
    }

    AnimatedVisibility(
        visibleState = transitionState,
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
                    canSave = notesView.checkSavableChecklist()
                )
            }
        ) { padding ->
            Column (
                modifier = Modifier
                    .background(Color(0xFF1F3548))
                    .fillMaxSize()
                    .padding(padding),
            ) {

                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(vertical = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .background(color = Color(0xFF1F3548))
                        .padding(20.dp)
                ) {
                    items(
                        items = notesView.temporaryCheckList,
                    ) {
                        CheckList(
                            checkList = CheckList(it.note,it.strike),
                            editText = {
                                focusRequester.requestFocus()
                                notesView.editChecklistEntry(
                                    it.note, notesView.temporaryCheckList.indexOf(it)
                                )
                            },
                            isComplete = {
                                if(it.strike == 0) { it.strike = 1; +1 } else { it.strike = 0; -1 }
                            },
                        )
                    }
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 5.dp)
                        ){
                            CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false){
                                Icon(
                                    tint = Color(0xFFD9A15B),
                                    imageVector = Icons.Outlined.CheckBoxOutlineBlank,
                                    contentDescription = "stringRes",
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                            }
                            TextField(
                                value = myUiState.checklistEntry,
                                onValueChange = { notesView.checklistEntry(it) },
                                placeholder = { Text(stringResource(R.string.searchnotes)) },
                                singleLine = true,
                                colors =
                                TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    placeholderColor = Color.LightGray,
                                    textColor = Color.White,
                                    cursorColor = Color(0xFFD9A15B)
                                ),
                                modifier = Modifier
                                    .focusRequester(focusRequester),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        notesView.addOrEditEntry()
                                    }
                                )
                            )
                        }
                    }
                }
            }
            BackHandler(onBack = { returnAndSave() })
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CheckList(checkList: CheckList, editText:()-> Unit, isComplete:()-> Int){
    var checkListEntry by remember { mutableStateOf(checkList.strike) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { editText() }
    ){
        CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false){
            IconButton(
                modifier = Modifier.size(30.dp),
                onClick = { checkListEntry = isComplete() }
            ) {
                Icon(
                    tint = Color(0xFFD9A15B),
                    imageVector = if (checkListEntry == 1) Icons.Outlined.CheckBox else Icons.Outlined.CheckBoxOutlineBlank,
                    contentDescription = "stringRes",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Text(
            text = checkList.note,
            color = Color.White,
            style = if(checkListEntry == 1) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle(textDecoration = TextDecoration.None)
        )
    }
}





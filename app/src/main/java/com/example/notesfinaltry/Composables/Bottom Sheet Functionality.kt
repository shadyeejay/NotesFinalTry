package com.example.notesfinaltry.Composables
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notesfinaltry.Data.NotesViewModel
import com.example.notesfinaltry.Database.Note
import com.example.notesfinaltry.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun BottomTools(
    coScope: CoroutineScope,
    focusRequester: FocusRequester,
    keyboardController: SoftwareKeyboardController,
    focusManager: FocusManager,
    navController: NavController,
    noteView: NotesViewModel,
    allNotes: List<Note>,
    allChecklist: List<Note>,
    searchQuery: String,
    searchQueryIt:(String) -> Unit,
    deleteAll: () -> Unit,
    deleteSelected: () -> Unit,
    addNote: () -> Unit,
    addCheckList: () -> Unit
) {

    val myUiState by noteView.uiState.collectAsState()
    val sheetState = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetState)
    if (!myUiState.sheetState) LaunchedEffect(Unit) { sheetState.collapse() }
    val listStateNotes = rememberLazyListState()
    val listStateCheckList = rememberLazyListState()

    BottomSheetScaffold(
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetBackgroundColor = Color(0xFF2B4053),
        drawerElevation = 800.dp,
        sheetPeekHeight = 50.dp,
        backgroundColor = Color.Transparent,
        scaffoldState = scaffoldState,
        modifier = Modifier
            .fillMaxSize()
            .heightIn(600.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        coScope.launch {
                            keyboardController.hide()
                            focusManager.clearFocus()
                            noteView.clearSearchQuery()
                            sheetState.collapse()
                        }
                    }
                )
            },
        sheetContent = {
            BottomNavigation(
                focusManager = focusManager,
                focusRequester = focusRequester,
                deleteAll = deleteAll,
                deleteSelected = deleteSelected,
                search = searchQuery,
                searching = searchQueryIt ,
                noteView = noteView,
                addNote = { noteView.clearValues(); addNote() },
                addCheckList = { addCheckList() },
                focusToTop = {
                    coScope.launch {
                        delay(300)
                        listStateNotes.animateScrollToItem(0)
                    }
                    coScope.launch {
                        delay(100)
                        listStateCheckList.animateScrollToItem(0)
                    }
                },
                moreOptions = {
                    coScope.launch {
                        when {
                            sheetState.isExpanded && !myUiState.showContent -> {
                                noteView.showContent(true)
                                sheetState.expand()
                            }
                            sheetState.isExpanded -> {
                                noteView.showContent(false)
                                keyboardController.hide()
                                focusManager.clearFocus()
                                noteView.clearSearchQuery()
                                sheetState.collapse()
                            }
                            sheetState.isCollapsed -> {
                                noteView.showContent(true)
                                sheetState.expand()
                            }
                        }
                    }
                },
                searchOptions = {
                    coScope.launch {
                        when {
                            sheetState.isExpanded && myUiState.showContent -> {
                                noteView.showContent(false)
                                focusRequester.requestFocus()
                            }
                            sheetState.isCollapsed -> {
                                delay(20)
                                noteView.showContent(false)
                                sheetState.expand()
                                focusRequester.requestFocus()
                            }
                            else -> {
                                noteView.showContent(false)
                                keyboardController.hide()
                                focusManager.clearFocus()
                                noteView.clearSearchQuery()
                                sheetState.collapse()
                            }
                        }
                    }
                }
            )
            BackHandler (
                onBack = {
                    keyboardController.hide()
                    focusManager.clearFocus()
                    coScope.launch{ sheetState.collapse() }
                }
            )
        }
    )
    { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
        ){
            AllChecklistScrollable(
                navController = navController,
                noteView = noteView ,
                notesList = allChecklist ,
                listState = listStateCheckList
            )
            AllNotesScrollable(
                navController = navController,
                noteView = noteView,
                notesList = allNotes,
                listState = listStateNotes
            )
        }
    }
}

@Composable
fun ButtonDesign(
    imageVector: ImageVector,
    stringRes: String,
    pullUp:() -> Unit,
    modifier: Modifier = Modifier,
    size: Dp
) {
    IconButton(
        modifier = modifier,
        onClick = { pullUp() }
    ) {
        Icon (
            tint = Color(0xFFD9A15B),
            imageVector = imageVector,
            contentDescription = stringRes,
            modifier = Modifier
                .size(size)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomNavigation(
    noteView: NotesViewModel,
    search: String,
    searching:(String) -> Unit,
    deleteAll: () -> Unit,
    deleteSelected: () -> Unit,
    moreOptions:() -> Unit,
    searchOptions: () -> Unit,
    addNote: () -> Unit,
    addCheckList:() -> Unit,
    focusToTop: () -> Unit,
    focusRequester: FocusRequester,
    focusManager: FocusManager,
    modifier: Modifier = Modifier
) {
    val myUiState by noteView.uiState.collectAsState()
    var expandedPopup by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = noteView.temporaryDeleteMemory.isEmpty(),
        transitionSpec = {
           slideIntoContainer(animationSpec = tween(350), towards = AnimatedContentScope.SlideDirection.Up) with
                    slideOutOfContainer(animationSpec = tween(350), towards = AnimatedContentScope.SlideDirection.Down) using
                    SizeTransform(clip = false)
        }
    ) { targetExpanded ->
        if (targetExpanded) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ButtonDesign(
                        modifier = Modifier.weight(1f),
                        imageVector = Icons.Outlined.Search,
                        stringRes = stringResource(R.string.search),
                        size = 25.dp,
                        pullUp = { searchOptions() }
                    )
//                    Divider(
//                        Modifier
//                            .fillMaxHeight(0.065f)
//                            .width(1.dp),
//                    )
                    ButtonDesign(
                        modifier = Modifier.weight(1f),
                        imageVector = Icons.Outlined.AddCircle,
                        stringRes = stringResource(R.string.addnote),
                        size = 25.dp,
                        pullUp = { expandedPopup = true }
                    )
//                    Divider(
//                        Modifier
//                            .fillMaxHeight(0.065f)
//                            .width(1.dp),
//                    )
                    ButtonDesign(
                        modifier = Modifier.weight(1f),
                        imageVector = Icons.Outlined.MoreVert,
                        stringRes = stringResource(R.string.moreoptions),
                        size = 25.dp,
                        pullUp = { moreOptions() }
                    )
                }
                Divider()
                TextField(
                    value = search,
                    onValueChange = { searching(it) },
                    placeholder = { Text(stringResource(R.string.searchnotes)) },
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            searchOptions()
                        }
                    ),
                    colors =
                    TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFD9A15B),
                        unfocusedBorderColor = Color(0xFFD9A15B),
                        placeholderColor = Color.LightGray,
                        textColor = Color.White,
                        cursorColor = Color(0xFFD9A15B)

                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
                if(myUiState.showContent) {
                    Spacer(modifier = Modifier.height(10.dp))
                    SortByOptions(noteView = noteView, focusToTop = focusToTop)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

        } else DeleteOptions(deleteSelected = deleteSelected, selectAll = deleteAll)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.BottomCenter),
    ) {
        DropdownMenu(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(color = Color(0xFF1F3548))
                .border(1.dp, Color(0XFF495057)),
            expanded = expandedPopup,
            onDismissRequest = { expandedPopup = false },
            offset = DpOffset((-57).dp,0.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                NoteSelectionButton(
                    text = "Note",
                    navigateToPage = {
                        expandedPopup = false
                        addNote()
                    }
                )
                Divider()
                NoteSelectionButton(
                    text = "Checklist",
                    navigateToPage = {
                        addCheckList()
                    }
                )
            }
        }
    }
}

@Composable
fun DeleteOptions(
    deleteSelected:() -> Unit,
    selectAll:() -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            ButtonDesign(
                modifier = Modifier.weight(1f),
                imageVector = Icons.Outlined.SelectAll,
                stringRes = stringResource(R.string.DeleteSelected),
                size = 25.dp,
                pullUp = { selectAll() }
            )

            Divider(
                Modifier
                    .fillMaxHeight(0.065f)
                    .width(1.dp),
            )

            ButtonDesign(
                modifier = Modifier.weight(1f),
                imageVector = Icons.Outlined.DeleteSweep,
                stringRes = stringResource(R.string.DeleteSelected),
                size = 25.dp,
                pullUp = { deleteSelected() }
            )
        }
        Divider()
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SortByOptions(
    noteView: NotesViewModel,
    focusToTop:() -> Unit
) {
    val myUiState by noteView.uiState.collectAsState()

    AnimatedContent(
        targetState = myUiState.toggle,
        transitionSpec = {
            fadeIn(animationSpec = tween(200)) with
                    fadeOut(animationSpec = tween(200)) using
                    SizeTransform(clip = false)
        }
    ) { targetToggle ->
        Row(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                tint = Color(0xFFD9A15B),
                modifier = Modifier
                    .size(25.dp),
                imageVector = Icons.Outlined.Sort,
                contentDescription = stringResource(R.string.sortby)
            )
            Spacer(Modifier.width(10.dp))
            Card(
                modifier = Modifier.height(30.dp),
                shape = RoundedCornerShape(28),
                border = BorderStroke(1.dp, Color(0XFF495057)),
                backgroundColor = Color(0xFF2B4053),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (!targetToggle) Color.LightGray else Color(0xFF2B4053)),
                        onClick = { noteView.updateStates(false);focusToTop() }
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = stringResource(R.string.newest),
                            fontSize = 10.sp,
                            color = if(!targetToggle) Color(0xFF1F3548) else Color.White,
                            maxLines = 1
                        )
                    }
                    Divider(
                        Modifier
                            .fillMaxHeight(0.065f)
                            .width(1.dp)
                    )
                    IconButton(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (targetToggle) Color.LightGray else Color(0xFF2B4053)),
                        onClick = { noteView.updateStates(true);focusToTop() }
                    ) {
                        Text(
                            text = stringResource(R.string.oldest),
                            fontSize = 10.sp,
                            color = if(targetToggle) Color(0xFF1F3548) else Color.White,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NoteSelectionButton(
    text:String,
    modifier: Modifier = Modifier,
    navigateToPage:() -> Unit
) {
    CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false){
        IconButton(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            onClick = {
                navigateToPage()
            }
        ) {
            Text(
                text = text,
                color = Color(0xFFD9A15B),
                fontSize = 18.sp
            )
        }
    }
}


package com.example.notesfinaltry.Composables
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.notesfinaltry.Data.NotesViewModel
import com.example.notesfinaltry.Data.PageNav
import com.example.notesfinaltry.Database.Note

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllChecklistScrollable(
    navController: NavController,
    noteView: NotesViewModel,
    notesList: List<Note>,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    var disable by remember { mutableStateOf(true) }
    val haptic = LocalHapticFeedback.current

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.background(color = Color(0xFF1F3548))
    ) {
        items(
            items = notesList,
            key = { checklist -> checklist.uid!! },

        ) { checklist ->
            Column(
                modifier = Modifier
                    .padding(
                        start = if(notesList.indexOf(checklist) == 0) {20.dp} else {0.dp},
                        end = if(notesList.indexOf(checklist) == notesList.lastIndex) {20.dp} else {0.dp}
                    )
                    .animateItemPlacement(
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    ),
            ) {
                ChecklistCards(
                    noteView = noteView,
                    onEditClick = {
                        if(disable) {
                            if (noteView.temporaryDeleteMemory.isEmpty()){
                                disable = false
                                noteView.clearValues()
                                noteView.temporaryCheckList.clear()
                                noteView.sheetState(false)
                                checklist.uid?.let { noteView.uid(it) }
                                checklist.checkList.let { it?.let { it1 -> noteView.toDoList(it1) } }
                                navController.navigate(route = PageNav.AddChecklist.name)
                                noteView.clearSearchQuery()
                            } else noteView.deleteTally(checklist)
                        }
                    },
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        noteView.deleteTally(checklist)
                    },
                    note = checklist
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllNotesScrollable(
    navController: NavController,
    noteView: NotesViewModel,
    notesList: List<Note>,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    var disable by remember { mutableStateOf(true) }
    val haptic = LocalHapticFeedback.current

    LazyColumn(
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .background(color = Color(0xFF1F3548))
            .fillMaxSize()
    ) {
        items(
            items = notesList,
            key = { notes -> notes.uid!! }
        ) {
                notes ->
            Column(
                modifier = Modifier
                    .animateItemPlacement(
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    ),
            ) {
                NoteCards(
                    noteView = noteView,
                    note = notes,
                    onLongPress = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        noteView.deleteTally(notes)
                    },
                    onEditClick = {
                        if(disable) {
                            if (noteView.temporaryDeleteMemory.isEmpty()){
                                disable = false
                                noteView.sheetState(false)
                                notes.uid?.let { noteView.uid(it) }
                                notes.header?.let { noteView.header(it) }
                                notes.note?.let { noteView.note(it) }
                                navController.navigate(route = PageNav.AddNote.name)
                                noteView.clearSearchQuery()
                            } else noteView.deleteTally(notes)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ChecklistCards(
    noteView: NotesViewModel,
    onEditClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    note: Note
){
    val primaryColor = Color(0xFF2B4053)
    val selectedColor = Color(0xFFD9A15B)
    var selected by remember { (mutableStateOf(noteView.temporaryDeleteMemory.contains(note))) }
    selected = noteView.temporaryDeleteMemory.contains(note)
    Card(
        border = (BorderStroke(1.dp,Color(0XFF495057))),
        backgroundColor = if (selected) selectedColor else primaryColor,
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onEditClick() },
                    onLongPress = { onLongPress() }
                )
            }
            .height(138.dp)
            .width(200.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            note.header?.let {
                Text(
                    text = it,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                modifier = Modifier.fillMaxHeight(0.75f)
            ) {
                note.checkList?.let{
                    val x = note.checkList.size
                    for(checklistItems in note.checkList.subList(0,if(x > 3) 3 else x)){
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFFD9A15B),
                                imageVector = if (checklistItems.strike == 1) Icons.Outlined.CheckBox else Icons.Outlined.CheckBoxOutlineBlank,
                                contentDescription = "Test"
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = checklistItems.note,
                                fontSize = 16.sp,
                                color = Color.LightGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = if(checklistItems.strike == 1) TextStyle(textDecoration = TextDecoration.LineThrough) else TextStyle(textDecoration = TextDecoration.None)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "01 January 2023",
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun NoteCards(
    noteView: NotesViewModel,
    onEditClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
    note: Note
){
    val primaryColor = Color(0xFF2B4053)
    val selectedColor = Color(0xFFD9A15B)
    var selected by remember { (mutableStateOf(noteView.temporaryDeleteMemory.contains(note))) }
    selected = noteView.temporaryDeleteMemory.contains(note)

    Card(
        border = (BorderStroke(1.dp,Color(0XFF495057))),
        backgroundColor = if (selected) selectedColor else primaryColor,
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onEditClick() },
                    onLongPress = { onLongPress() }
                )
            }
            .fillMaxWidth(0.95f)
            .height(78.5.dp)
            .padding(start = 10.dp, end = 10.dp)

    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.7f),
                verticalArrangement = Arrangement.SpaceBetween,

            ) {
                note.header?.let {
                    Text(
                        text = it,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                note.note?.let {
                    Text(
                        text = it,
                        fontSize = 15.sp,
                        color = Color.LightGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Column(
                modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                note.date?.let {
                    Text(
                        text = it,
                        fontSize = 10.sp,
                        color = Color.LightGray,
                        maxLines = 1,
                    )
                }
                if (selected) {
                    Icon(
                        imageVector = Icons.Outlined.CheckBox,
                        contentDescription = null,
                        tint = Color(0xFF1F3548)
                    )
                }
            }
        }
    }
}

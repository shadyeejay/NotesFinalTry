package com.example.notesfinaltry.Composables
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIos
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notesfinaltry.Data.NotesViewModel
import com.example.notesfinaltry.R


@Composable
fun TopNavigation(
    backButton: () -> Unit,
    deleteNote: () -> Unit,
    saveNote:() -> Unit,
    canSave: Boolean,
    noteView: NotesViewModel
) {
    val primaryColor = Color(0xFF1F3548)
    val iconColor = Color(0xFFD9A15B)
    val myUiState by noteView.uiState.collectAsState()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TopAppBar(
            title = { Text(text = "") },
            backgroundColor = primaryColor,
            navigationIcon = {
                IconButton(onClick = { backButton() }) {
                    Icon(
                        modifier = Modifier.size(19.dp),
                        imageVector = Icons.Outlined.ArrowBackIos,
                        contentDescription = stringResource(R.string.backbutton),
                        tint = iconColor
                    )
                }
            },
            actions = {
                Card(
                    modifier = Modifier
                        .height(30.dp)
                        .padding(end = 10.dp),
                    shape = RoundedCornerShape(28),
                    border = BorderStroke(1.dp, Color(0XFF495057)),
                    backgroundColor = Color(0xFF2B4053),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.4f),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        IconButton(
                            modifier = Modifier.weight(1f),
                            onClick = { if(canSave) saveNote() }
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.Outlined.Save,
                                contentDescription = stringResource(R.string.deletenote),
                                tint = if (canSave) Color(0xFFD9A15B) else Color(0XFF495057)
                            )
                        }
                        Divider(
                            Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                        )
                        IconButton(
                            modifier = Modifier.weight(1f),
                            onClick = { deleteNote() }
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.deletenote),
                                tint = iconColor
                            )
                        }
                    }
                }
            },
        )
    }
}



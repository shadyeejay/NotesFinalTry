package com.example.notesfinaltry.Composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.notesfinaltry.R


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ConfirmDelete(
    popUp: Boolean,
    cancel:() -> Unit,
    confirmDelete:() -> Unit
){
    if(popUp){
        AlertDialog(
            modifier = Modifier
                .width(260.dp)
                .height(140.dp),
            backgroundColor = Color(0xFF2B4053),
            onDismissRequest = { cancel() },
            buttons = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = { confirmDelete();cancel() }
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.deleteConfirm),
                            color = Color(0xFFD9A15B),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 19.sp
                        )
                    }
                    IconButton(onClick = { cancel() },) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.deleteCancel),
                            color = Color.White
                        )
                    }
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false),
            text = {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.LightGray,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        text = stringResource(R.string.deleteMessage),
                    )
                }
            }
        )
    }
}
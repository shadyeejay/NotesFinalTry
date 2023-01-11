package com.example.notesfinaltry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.notesfinaltry.ui.theme.NotesFinalTryTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesFinalTryTheme {
                MainComposable()
            }
        }
    }
}
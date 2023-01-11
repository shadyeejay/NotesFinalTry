package com.example.notesfinaltry.Composables
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.notesfinaltry.R
import com.example.notesfinaltry.Data.PageNav
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.Loading))
    }
    LaunchedEffect(Unit) {
        delay(300)
        navController.navigate(route = PageNav.AllNotes.name)
    }
}
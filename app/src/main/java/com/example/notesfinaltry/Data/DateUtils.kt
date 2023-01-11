package com.example.notesfinaltry.Data

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateUtils{

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yy")
    @RequiresApi(Build.VERSION_CODES.O)
    val current: String? = LocalDateTime.now().format(formatter)

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime():String{
        return current.toString()
    }
}
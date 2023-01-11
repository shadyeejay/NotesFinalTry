package com.example.notesfinaltry.Database

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.notesfinaltry.Composables.CheckList
import java.util.Date



@Entity
data class Note(
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name = "header") val header: String?,
    @ColumnInfo(name = "note") val note: String?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "checkList") val checkList: ArrayList<CheckList>?
)

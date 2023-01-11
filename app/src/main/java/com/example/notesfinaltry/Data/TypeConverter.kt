package com.example.notesfinaltry.Composables

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class CheckList(
    val note: String,
    var strike: Int,
)


inline fun <reified T> Gson.fromJson(json: String) =
    fromJson<T>(json, object : TypeToken<T>() {}.type)


class ArrayListConverter {

    @TypeConverter
    fun fromStringArrayList(value: ArrayList<CheckList>?): String {

        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringArrayList(value: String): ArrayList<CheckList>? {
        return try {
            Gson().fromJson<ArrayList<CheckList>>(value) //using extension function
        } catch (e: Exception) {
            arrayListOf()
        }
    }
}
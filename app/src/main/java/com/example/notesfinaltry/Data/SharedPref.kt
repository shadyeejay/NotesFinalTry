package com.example.notesfinaltry.Data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class SharedPref(private val context: Context) {

    companion object{
        private val Context.datastore: DataStore<Preferences> by preferencesDataStore("sort")
        val SORT_BY = booleanPreferencesKey("on")
    }


    //get sort
    val getSort: Flow<Boolean> = context.datastore.data
        .map {
                preferences -> preferences[SORT_BY] ?: false
        }


    // save sort
    suspend fun saveSort(sort:Boolean) {
        context.datastore.edit{
            preferences -> preferences[SORT_BY] = sort
        }
    }

}
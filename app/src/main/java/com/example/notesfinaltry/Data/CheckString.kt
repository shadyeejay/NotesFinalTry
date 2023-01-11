package com.example.notesfinaltry.Composables

class CheckString {

    fun checkString(string: String): String? {
        return string.ifEmpty { null }
    }

}

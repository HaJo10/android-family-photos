package com.shellmonger.apps.familyphotos.extensions

import android.widget.EditText

fun EditText.getContent(): String {
    return this.text.toString()
}
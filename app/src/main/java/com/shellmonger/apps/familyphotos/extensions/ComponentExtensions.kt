package com.shellmonger.apps.familyphotos.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Convenience method to get the content of the EditText control
 */
fun EditText.getContent(): String {
    return this.text.toString()
}

/**
 * Convenience method used to validate the textual content
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
    })
}

/**
 * Validate the content of the edit text
 */
fun EditText.validate(validator: (String) -> Boolean, message: String) {
    this.afterTextChanged {
        this.error = if (validator(it)) null else message
    }
    this.error = if (validator(this.getContent())) null else message
}
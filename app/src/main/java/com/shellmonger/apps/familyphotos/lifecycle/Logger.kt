package com.shellmonger.apps.familyphotos.lifecycle

import android.util.Log

/**
 * Convenience class that wraps the Android Log to provide className as the tag.
 */
class Logger(private val className: String) {
    fun debug(message: String) = Log.d(className, message)

    fun info(message: String) = Log.i(className, message)

    fun warning(message: String) = Log.w(className, message)

    fun error(message: String, error: Throwable? = null) {
        val errorMessage = if (error != null) ": ${error.message}" else ""
        Log.e(className, "$message$errorMessage")
    }

    fun wtf(message: String, error: Throwable? = null) {
        if (error != null) {
            val sw = java.io.StringWriter()
            val pw = java.io.PrintWriter(sw)
            error.printStackTrace(pw)
            pw.close()
            Log.wtf(className, "$message\n$sw", error)
        } else {
            Log.wtf(className, message)
        }
    }
}
package com.shellmonger.apps.familyphotos.extensions

import android.support.v7.view.menu.MenuBuilder
import android.util.Log
import android.view.MenuItem

fun MenuItem.getShowAsAction(): Int {
    try {
        val field = this.javaClass.getDeclaredField("mShowAsAction")
        field.isAccessible = true
        return field.getInt(this)
    } catch (ignored: Exception) {
        Log.d("MenuBuilder", "getShowAsAction() failed: ${ignored.message}")
        return -1
    }
}

fun MenuBuilder.setIconsVisible(isVisible: Boolean) {
    try {
        val field = this.javaClass.getDeclaredField("mOptionalIconsVisible")
        field.isAccessible = true
        field.setBoolean(this, true)
    } catch (ignored: Exception) {
        Log.d("MenuBuilder", "setIconsVisible($isVisible) failed: ${ignored.message}")
    }
}
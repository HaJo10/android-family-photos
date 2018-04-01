package com.shellmonger.apps.familyphotos.extensions

import android.view.MenuItem

/**
 * Returns the value of showAsAction
 */
fun MenuItem.getShowAsAction(): Int {
    var f = this.javaClass.getDeclaredField("mShowAsAction")
    f.isAccessible = true
    return f.getInt(this)
}
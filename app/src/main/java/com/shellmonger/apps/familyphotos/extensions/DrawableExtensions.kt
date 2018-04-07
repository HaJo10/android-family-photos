package com.shellmonger.apps.familyphotos.extensions

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable

fun Drawable.setIconColor(color: Int) {
    mutate()
    setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
}

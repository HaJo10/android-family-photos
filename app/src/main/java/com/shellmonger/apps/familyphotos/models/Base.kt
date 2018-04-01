package com.shellmonger.apps.familyphotos.models

import java.util.*

abstract class Base {
    var id: String = UUID.randomUUID().toString()
    var created: Long = System.currentTimeMillis()
    var updated: Long = System.currentTimeMillis()
    var deleted: Boolean = false

    override fun equals(other: Any?): Boolean = (other is Base && other.id == id)
}
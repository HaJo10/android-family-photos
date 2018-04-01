package com.shellmonger.apps.familyphotos.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import com.google.gson.Gson
import java.io.File

class Post: Base() {
    var filename: String? = null
    var caption: String = ""

    /**
     * Gets the bitmap associated with the filename
     */
    fun getBitmap(): Bitmap = BitmapFactory.decodeFile(filename)

    /**
     * Saves the bitmap to a file
     */
    fun setBitmap(bitmap: Bitmap) {
        val directory = File("${Environment.getExternalStorageDirectory()}/pictures")
        directory.mkdirs()      // Ensure the directories exist

        // The file is based on the ID so it is unique
        val file = File(directory, "$id.png")
        filename = file.absolutePath

        file.createNewFile()
        with (file.outputStream()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, this)
            this.flush()
            this.close()
        }
    }

    /**
     * Convert the current object to a JSON representation of the object
     */
    override fun toString(): String = Gson().toJson(this)

    companion object {
        /**
         * Convert the provided string from JSON to an object of the Post type.
         */
        fun fromJson(json: String): Post = Gson().fromJson(json, Post::class.java)
    }
}
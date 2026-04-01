package com.forge.bright

import android.Manifest
import android.os.Environment
import android.content.Context
import android.net.Uri
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

const val MODEL_EXTENSION = ".litertlm"
fun hasPreferredModelExtension(path: Any): Boolean {
    if (path is String) {
        return path.endsWith(MODEL_EXTENSION)
    }
    if (path is File) {
        return path.name.endsWith(MODEL_EXTENSION)
    }

    if (path is Path) {
        return path.endsWith(MODEL_EXTENSION)
    }

    if (path is Uri) {
        return path.toString().endsWith(MODEL_EXTENSION)
    }
    return true
}

fun getModelDirectoryAbsolutePath(): String {
    return Paths.get(Environment.getExternalStorageDirectory().toString(), getModelDirectory())
        .toAbsolutePath().toString()
}

fun getModelDirectory(): String {
    return "MyHappyBot/models/"
}

const val REQUEST_STORAGE_PERMISSIONS = 101
val STORAGE_PERMISSIONS = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)
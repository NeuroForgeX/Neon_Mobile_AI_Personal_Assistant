package com.forge.bright

import android.Manifest
import android.os.Environment
import android.content.Context
import java.nio.file.Paths

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
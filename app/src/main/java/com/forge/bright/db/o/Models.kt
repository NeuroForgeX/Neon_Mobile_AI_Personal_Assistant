package com.forge.bright.db.o

import android.content.Context
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.forge.bright.utils.FileTransferHelper
import java.io.File

private const val TAG = "Models.kt"

// @formatter:off
enum class ModelType(val value: String) {
    GGUF("gguf"),
    LiteRTLM("litertlm")
}

@Entity(tableName = "models", indices = [Index(value = ["id"], unique = true)])
data class Model(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String, // filename with extension
    val shortName: String,
    val description: String,
    val size: String,
    val downloadUrl: String = "",
    val owner: String,
    val localFilePath: String = "",
    val type: ModelType,
    val createdAt: Long = System.currentTimeMillis())
// @formatter:on

// Extension functions for determining model status
fun Model.isOfflineAvailable(): Boolean {
    return localFilePath.trim().isNotEmpty()
}

fun Model.isOfflineDownloaded(): Boolean {
    return localFilePath.trim().isEmpty() && downloadUrl.trim().isEmpty()
}

fun Model.buildLocalFileDirectory(context: Context, fileHelper: FileTransferHelper): String {
    if (isOfflineAvailable()) {
        return File(localFilePath).parent!!
    }
    return fileHelper.createInternalDirectory(context, owner, name.substringBeforeLast(".")).absolutePath
}

fun Model.buildLocalFilePath(context: Context, fileHelper: FileTransferHelper): String {
    if (isOfflineAvailable()) {
        return localFilePath
    }
    return fileHelper.createInternalDirectory(context, owner, name.substringBeforeLast("."), name).absolutePath
}

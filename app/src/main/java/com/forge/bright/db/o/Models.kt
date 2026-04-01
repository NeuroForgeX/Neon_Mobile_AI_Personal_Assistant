package com.forge.bright.db.o

import android.net.Uri
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// @formatter:off
enum class ModelType(val value: String) {
    GGUF("gguf"),
    LiteRTLM("litertlm")
}

@Entity(tableName = "models", indices = [Index(value = ["id"], unique = true)])
data class Model(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val name: String,
    val shortName: String,
    val description: String,
    val size: String,
    val downloadUrl: String = "",
    val owner: String,
    val localUri: Uri = Uri.EMPTY,
    val type: ModelType,
    val createdAt: Long = System.currentTimeMillis())
// @formatter:on

// Extension functions for determining model status
fun Model.isOfflineAvailable(): Boolean {
    return localUri.toString().isNotEmpty()
}

fun Model.isDownloading(): Boolean {
    // TODO: Check download progress table when implemented
    // For now, we'll assume models with empty localUri are not downloaded
    return false
}

fun Model.getModelStatus(): ModelUIStatus {
    return when {
        isOfflineAvailable() -> ModelUIStatus.OFFLINE_AVAILABLE
        isDownloading() -> ModelUIStatus.DOWNLOADING
        else -> ModelUIStatus.UNAVAILABLE
    }
}

// UI status enum matching the existing ModelType in UI
enum class ModelUIStatus {
    OFFLINE_AVAILABLE, UNAVAILABLE, DOWNLOADING
}

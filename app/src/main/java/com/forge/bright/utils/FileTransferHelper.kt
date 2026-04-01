package com.forge.bright.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileOutputStream

object FileTransferHelper {

    private const val TAG = "FileTransferHelper"

    /**
     * Get the internal cache directory
     */
    fun getInternalCacheDir(context: Context): File {
        return context.cacheDir
    }

    /**
     * Copy a single file from URI to internal directory
     */
    fun copyFileToInternal(context: Context, uri: Uri, targetFileName: String): String? {
        return try {
            val internalDir = getInternalCacheDir(context)
            val targetFile = File(internalDir, targetFileName)

            // Copy file content
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }

            Log.d(TAG, "File copied to: ${targetFile.absolutePath}")
            targetFile.absolutePath

        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy file: ${e.message}", e)
            null
        }
    }

    /**
     * Copy entire directory structure from URI to internal cache directory
     * Returns list of all copied file paths
     */
    fun copyDirectory(context: Context, treeUri: Uri): List<String>? {
        return try {
            val documentFile = DocumentFile.fromTreeUri(context, treeUri)
            if (documentFile == null || !documentFile.exists()) {
                Log.e(TAG, "Invalid tree URI")
                return null
            }

            val internalCacheDir = getInternalCacheDir(context)

            // Get the directory name from the URI
            val dirName = documentFile.name ?: "directory"
            val targetDir = File(internalCacheDir, dirName)
            targetDir.mkdirs()

            // Copy all files and collect paths
            val copiedFiles = mutableListOf<String>()
            copyDirectoryAndCollectPaths(context, documentFile, targetDir, copiedFiles)

            Log.d(TAG, "Directory copied to: ${targetDir.absolutePath}")
            Log.d(TAG, "Total files copied: ${copiedFiles.size}")
            copiedFiles

        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy directory: ${e.message}", e)
            null
        }
    }

    /**
     * Recursively copy directory contents and collect all file paths
     */
    private fun copyDirectoryAndCollectPaths(
        context: Context,
        sourceDir: DocumentFile,
        targetDir: File,
        filePaths: MutableList<String>
    ) {
        sourceDir.listFiles()?.forEach { documentFile ->
            val targetFile = File(targetDir, documentFile.name ?: "unknown")

            if (documentFile.isDirectory) {
                targetFile.mkdirs()
                copyDirectoryAndCollectPaths(context, documentFile, targetFile, filePaths)
            } else {
                // Copy file
                context.contentResolver.openInputStream(documentFile.uri)?.use { input ->
                    FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                    }
                }
                // Add the file path to the list
                filePaths.add(targetFile.absolutePath)
                Log.d(TAG, "Copied: ${documentFile.name} -> ${targetFile.absolutePath}")
            }
        }
    }

    /**
     * Get all files with specific extension from copied directory
     */
    fun findFilesByExtension(dirPath: String, extension: String): List<String> {
        val dir = File(dirPath)
        if (!dir.exists()) {
            return emptyList()
        }

        val foundFiles = mutableListOf<String>()
        findFilesRecursive(dir, extension, foundFiles)
        return foundFiles
    }

    /**
     * Recursively search for files with specific extension
     */
    private fun findFilesRecursive(dir: File, extension: String, foundFiles: MutableList<String>) {
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                findFilesRecursive(file, extension, foundFiles)
            } else if (file.name.endsWith(extension, ignoreCase = true)) {
                foundFiles.add(file.absolutePath)
            }
        }
    }

    /**
     * Check if file exists in internal directory
     */
    fun fileExists(filePath: String): Boolean {
        return File(filePath).exists()
    }

    /**
     * Get directory path for a given directory name in cache
     */
    fun getCacheDirPath(context: Context, dirName: String): String {
        return File(getInternalCacheDir(context), dirName).absolutePath
    }

    /**
     * Create intent for selecting directory
     */
    fun createDirectoryPickerIntent(): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
    }

    /**
     * Create intent for selecting single file
     */
    fun createFilePickerIntent(): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/octet-stream"
            addCategory(Intent.CATEGORY_OPENABLE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
    }

    /**
     * Take persistent permission for a URI
     */
    fun takePersistentPermission(context: Context, uri: Uri) {
        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            Log.d(TAG, "Persistent permission granted for: $uri")
        } catch (e: Exception) {
            Log.w(TAG, "Could not take persistent permission: ${e.message}")
        }
    }

    /**
     * Clean up internal cache directory
     */
    fun cleanupCache(context: Context) {
        try {
            getInternalCacheDir(context).deleteRecursively()
            Log.d(TAG, "Cache directory cleaned up")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to cleanup cache: ${e.message}", e)
        }
    }
}


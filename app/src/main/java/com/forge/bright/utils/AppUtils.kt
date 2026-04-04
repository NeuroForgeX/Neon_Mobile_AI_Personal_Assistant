package com.forge.bright.utils

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.documentfile.provider.DocumentFile
import com.forge.bright.STORAGE_PERMISSIONS
import java.io.File

private const val TAG = "AppUtils.kt"

object AppUtils {

    fun hasStoragePermissions(context: Context): Boolean {
        return STORAGE_PERMISSIONS.all { permission ->
            checkSelfPermission(context, permission) == PERMISSION_GRANTED
        }
    }

    fun requestAllPermissions(activity: ComponentActivity) {
        val permissions = STORAGE_PERMISSIONS
        val permissionsToRequest = permissions.filter { permission ->
            checkSelfPermission(activity, permission) != PERMISSION_GRANTED
        }

        val requestPermissionLauncher = activity.registerForActivityResult(RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach { entry ->
                val permissionName = entry.key
                val isGranted = if (entry.value) "granted" else "denied"
                Toast.makeText(activity, "$permissionName $isGranted", Toast.LENGTH_SHORT).show()

            }

            // Check if all required permissions are granted
            val allPermissionsGranted = permissions.values.all { it }
            if (!allPermissionsGranted) {
                Toast.makeText(activity, "Storage permissions are required for this app to work properly", Toast.LENGTH_LONG).show()
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            Toast.makeText(activity, "All permissions already granted", Toast.LENGTH_SHORT).show()
        }
    }

    fun createMyFolder(context: Context, rootUri: Uri) {
        val rootDoc = DocumentFile.fromTreeUri(context, rootUri)

        // 1. Create the folder (check if it exists first)
        val myFolder = rootDoc?.findFile("MyHappyBot") ?: rootDoc?.createDirectory("MyHappyBot")

        // 2. Create a file inside that folder
        val newFile = myFolder?.createFile("text/plain", "hello.txt")

        // 3. Write data to the file
        newFile?.uri?.let { fileUri ->
            context.contentResolver.openOutputStream(fileUri)?.use { outputStream ->
                outputStream.write("Hello from MyHappyBot!".toByteArray())
            }
        }
    }

    // Trigger the picker (e.g., on button click)
    fun askUserForLocation(activity: ComponentActivity) {
        activity.registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
            if (uri != null) {
                // The user selected a directory. Now create your folder.
                createMyFolder(activity, uri)
            }
        }.launch(null)
    }

    fun createInternalStorageFolder(context: Context, folderName: String): File? {
        if (!hasStoragePermissions(context)) {
            Log.e(TAG, "Storage permissions not granted")
            return null
        }

        val customFolder = File(context.getExternalFilesDir(null), folderName)

        // Check if folder already exists, if not, create it
        if (!customFolder.exists()) {
            val success = customFolder.mkdirs() // mkdirs() creates directory and any necessary parent directories
            if (success) {
                Log.d(TAG, "Folder '$folderName' created successfully at: ${customFolder.absolutePath}")
            } else {
                Log.e(TAG, "Failed to create folder '$folderName'")
                return null
            }
        } else {
            Log.d(TAG, "Folder '$folderName' already exists at: ${customFolder.absolutePath}")
        }
        return customFolder
    }

    fun getAbsolutePath(context: Context, uri: Uri): String? {
        return try {
            when {
                uri.scheme == "file" -> uri.path
                DocumentsContract.isDocumentUri(context, uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    if (docId.startsWith("primary:")) {
                        val path = docId.substring(8) // Remove "primary:" prefix
                        "/storage/emulated/0/$path"
                    } else {
                        null
                    }
                }

                else -> {
                    // Try to get path from content resolver
                    val cursor = context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)
                    cursor?.use {
                        if (it.moveToFirst()) {
                            it.getString(it.getColumnIndexOrThrow(android.provider.OpenableColumns.DISPLAY_NAME))
                            // For content URIs, we might not be able to get absolute path directly
                            // Return the URI string as fallback
                            uri.toString()
                        } else null
                    } ?: uri.toString()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            uri.toString() // Fallback to URI string
        }
    }
}

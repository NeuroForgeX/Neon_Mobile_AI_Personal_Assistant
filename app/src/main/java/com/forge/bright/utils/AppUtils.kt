package com.forge.bright.utils

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import com.forge.bright.STORAGE_PERMISSIONS

private const val TAG = "AppUtils.kt"

object AppUtils {

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

}

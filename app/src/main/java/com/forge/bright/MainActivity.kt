package com.forge.bright

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.forge.bright.ai.ChatAssistant
import com.forge.bright.db.DefaultData.initializeModelsIfEmpty
import com.forge.bright.ui.navigation.AppNavigation
import com.forge.bright.ui.theme.MyHappyBotTheme
import com.forge.bright.utils.AppUtils
import com.forge.bright.utils.PreferencesManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val TAG = javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            initializeModelsIfEmpty(this@MainActivity)
        }
        // Enable edge-to-edge display
        enableEdgeToEdge()

        // Initialize PreferencesManager and load preferences
        PreferencesManager.initialize(this)

        // Request storage permissions when app starts
        AppUtils.requestAllPermissions(this)

        // Enable fullscreen with status bar
        enableFullscreen()

        setContent {
            MyHappyBotTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavigation(navController = navController, onModelConfigCheck = { hasModelConfigured ->
                        if (hasModelConfigured) {
                            ChatAssistant.load(this@MainActivity, PreferencesManager)
                        }
                    })
                }
            }
        }
    }

    private fun enableFullscreen() {
        // Show status bar but keep immersive experience
        window.insetsController?.let { controller ->
            // Show status bar
            controller.show(WindowInsets.Type.statusBars())
            // Hide navigation bar only
            controller.hide(WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        // Set status bar color to match app theme
        window.statusBarColor = getColor(android.R.color.holo_purple)

        // Make status bar icons visible (light icons on dark background)
        window.insetsController?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsets.Type.statusBars())
    }

    private fun getFileDetails(uri: Uri) {
        val projection = arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE)

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndexOrThrow(OpenableColumns.SIZE)

                val fileName = cursor.getString(nameIndex)
                val fileSize = cursor.getLong(sizeIndex) // size in bytes

                Log.d(TAG, "Name: $fileName, Size: $fileSize bytes")
            }
        }
    }
}

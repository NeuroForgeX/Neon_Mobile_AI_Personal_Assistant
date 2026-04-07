package com.forge.bright

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns.DISPLAY_NAME
import android.provider.OpenableColumns.SIZE
import android.util.Log
import android.view.WindowInsets.Type
import android.view.WindowInsets.Type.statusBars
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.forge.bright.ai.ChatAssistant.isLoaded
import com.forge.bright.ai.ChatAssistant.load
import com.forge.bright.db.DataAccess
import com.forge.bright.db.DataAccess.getAllModels
import com.forge.bright.db.DefaultData.initializeModelsIfEmpty
import com.forge.bright.db.o.Model
import com.forge.bright.ui.navigation.AppNavigation
import com.forge.bright.ui.theme.MyHappyBotTheme
import com.forge.bright.utils.AppUtils
import com.forge.bright.utils.PreferencesManager
import com.forge.bright.utils.PreferencesManager.saveModelInformation
import com.google.android.gms.tflite.java.TfLite
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val SPLASH_MIN_DURATION = 2000L // 2 seconds minimum
private const val TAG = "MainActivity.kt"

class MainActivity : ComponentActivity() {
    // Track if initialization is complete
    private var isReady = false
    private var hasOfflineModel = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize database synchronously before any UI setup
        DataAccess.initialize(this)

        // Install splash screen
        val splashScreen = installSplashScreen()

        // Keep splash screen visible until initialization is complete
        splashScreen.setKeepOnScreenCondition { !isReady }

        loadStringResources(this)
        // Start initialization process
        initializeApp()

        // Request storage permissions when app starts
        AppUtils.requestAllPermissions(this)

        // Enable fullscreen with status bar
        enableFullscreen()

        setContent {
            MyHappyBotTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    AppNavigation(navController = navController, onModelConfigCheck = { hasModelConfigured ->
                        if (hasModelConfigured && isReady) {
                            load(this@MainActivity, PreferencesManager)
                        }
                    }, skipSplash = false, startWithChat = hasOfflineModel)
                }
            }
        }
    }

    private fun initializeApp() {
        val startTime = System.currentTimeMillis()

        lifecycleScope.launch {
            try {
                // 1. Initialize PreferencesManager
                PreferencesManager.initialize(this@MainActivity)

                // 2. Initialize Play services LiteRT
                initializeTfLite()

                // 3. Initialize default models if needed
                initializeModelsIfEmpty(this@MainActivity)

                load(this@MainActivity, PreferencesManager)
                if (!isLoaded()) {
                    val models: List<Model> = getAllModels().first()
                    val availableModel = models.firstOrNull { it.localFilePath.trim().isNotEmpty() }
                    availableModel?.let { model ->
                        saveModelInformation(model)
                        load(this@MainActivity, PreferencesManager)
                    }
                }

                // 6. Wait for minimum splash duration
                val elapsedTime = System.currentTimeMillis() - startTime
                if (elapsedTime < SPLASH_MIN_DURATION) {
                    delay(SPLASH_MIN_DURATION - elapsedTime)
                }

                // 7. Mark as ready
                isReady = true

            } catch (e: Exception) {
                // Handle any initialization errors
                Log.d(TAG, "", e)
                hasOfflineModel = false
                isReady = true
            }
        }
    }

    private fun enableFullscreen() {
        // Show status bar but keep immersive experience
        window.insetsController?.let { controller ->
            // Show status bar
            controller.show(statusBars())
            // Hide navigation bar only
            controller.hide(Type.navigationBars())
            controller.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        // Make status bar icons visible (light icons on dark background)
        window.insetsController?.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, statusBars())
    }

    private fun getFileDetails(uri: Uri) {
        val projection = arrayOf(DISPLAY_NAME, SIZE)

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndexOrThrow(DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndexOrThrow(SIZE)

                val fileName = cursor.getString(nameIndex)
                val fileSize = cursor.getLong(sizeIndex) // size in bytes

                Log.d(TAG, "Name: $fileName, Size: $fileSize bytes")
            }
        }
    }

    private fun initializeTfLite() {
        // Initialize Play services LiteRT
        val initializeTask = TfLite.initialize(this)

        initializeTask.addOnSuccessListener {
            // Initialization complete: you can now create your interpreter
            Log.d(TAG, "Play services LiteRT initialized successfully")
            setupInterpreter()
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to initialize Play services runtime", e)
        }
    }

    private fun setupInterpreter() {
        // This method will be called after successful LiteRT initialization
        // You can add any additional setup logic here if needed
        Log.d(TAG, "Interpreter setup completed")
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    // Initialize routes for preview
    ROUTE_MAIN = "main"
    ROUTE_CHAT = "chat"
    ROUTE_MODEL_SETUP = "model_setup"
    ROUTE_SETTINGS = "settings"
    
    MyHappyBotTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            val navController = rememberNavController()
            AppNavigation(navController = navController, onModelConfigCheck = { _ -> }, skipSplash = true, startWithChat = false)
        }
    }
}

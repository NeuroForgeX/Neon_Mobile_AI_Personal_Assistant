package com.forge.bright

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.forge.bright.databinding.ActivityMainBinding
import com.forge.bright.utils.requestAllPermissions
import com.forge.bright.utils.PreferencesManager
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize PreferencesManager and load preferences
        preferencesManager = PreferencesManager(this)
        loadPreferences()

        // Enable fullscreen mode
        enableFullscreen()

        // Set up toolbar (required for navigation component)
        setSupportActionBar(binding.toolbar)

        // Hide toolbar for fullscreen experience
        supportActionBar?.hide()
        binding.toolbar.visibility = View.GONE

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Request storage permissions when app starts
        requestAllPermissions(this)
    }

    private fun enableFullscreen() {
        // Hide system bars (status bar and navigation bar)
        window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun getFileDetails(uri: Uri) {
        val projection = arrayOf(
            OpenableColumns.DISPLAY_NAME,
            OpenableColumns.SIZE
        )

        contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndexOrThrow(OpenableColumns.SIZE)

                val fileName = cursor.getString(nameIndex)
                val fileSize = cursor.getLong(sizeIndex) // size in bytes

                Log.d("FileDetails", "Name: $fileName, Size: $fileSize bytes")
            }
        }
    }

    private fun loadPreferences() {
        // Check if model is configured
        if (preferencesManager.hasModelConfigured()) {
            // Model is configured, stay on ChatScreen (which is the start destination)
            val modelUri = preferencesManager.modelUri
            val fileDetails = getFileDetails(modelUri!!.toUri())
            Log.d(localClassName, "Model URI: $modelUri, File Details: $fileDetails")
        } else {
            // No model configured, navigate to ModelSetupScreen
            Log.d("MainActivity", "No model configured, navigating to setup")

            // Navigate to ModelSetupScreen after a short delay to ensure navigation controller is ready
            binding.root.postDelayed({
                try {
                    val navController = findNavController(R.id.nav_host_fragment_content_main)
                    navController.navigate(R.id.action_FirstFragment_to_SecondFragment)
                } catch (e: Exception) {
                    Log.e("MainActivity", "Navigation failed: ${e.message}")
                }
            }, 500) // 500ms delay
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}

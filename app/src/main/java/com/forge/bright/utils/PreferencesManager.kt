package com.forge.bright.utils

import android.content.Context
import android.util.Log
import androidx.core.content.edit

class PreferencesManager(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        Log.d(
            "PreferencesManager",
            "PreferencesManager initialized with context: ${context.javaClass.simpleName}"
        )
        // Force sync on initialization to ensure data is available
        forceSync()
    }

    companion object {
        private const val PREFS_NAME = "MyHappyBotPrefs"
        private const val KEY_MODEL_DIR = "model_uri"
        private const val KEY_MODEL_NAME = "model_name"
        private const val KEY_HAS_PROMPTED_FOR_FILE = "has_prompted_for_file"

    }

    var modelUri: String?
        get() = sharedPreferences.getString(KEY_MODEL_DIR, null)
        set(value) = sharedPreferences.edit { putString(KEY_MODEL_DIR, value) }

    var modelName: String?
        get() = sharedPreferences.getString(KEY_MODEL_NAME, null)
        set(value) = sharedPreferences.edit { putString(KEY_MODEL_NAME, value) }


    var hasPromptedForFile: Boolean
        get() = sharedPreferences.getBoolean(KEY_HAS_PROMPTED_FOR_FILE, false)
        set(value) = sharedPreferences.edit { putBoolean(KEY_HAS_PROMPTED_FOR_FILE, value) }


    fun saveModelInfo(nameOfModel: String, directoryOfModels: String) {
        Log.d("PreferencesManager", "Saving model uri=$directoryOfModels")
        val editor = sharedPreferences.edit()
        editor.putString(KEY_MODEL_DIR, directoryOfModels)
        editor.putString(KEY_MODEL_NAME, nameOfModel)
        editor.apply() // Asynchronous save

        // Also commit for immediate persistence (synchronous)
        val success = editor.commit()
        Log.d("PreferencesManager", "Model info saved successfully: $success")
    }

    fun forceSync() {
        // Force SharedPreferences to sync to disk
        try {
            sharedPreferences.edit().apply()
        } catch (e: Exception) {
            Log.e("PreferencesManager", "Failed to sync preferences", e)
        }
    }

    fun hasModelConfigured(): Boolean {
        return !modelUri.isNullOrEmpty()
    }
}

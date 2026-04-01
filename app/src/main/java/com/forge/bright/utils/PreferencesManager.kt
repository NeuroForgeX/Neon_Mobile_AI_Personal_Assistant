package com.forge.bright.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit

object PreferencesManager {
    private val TAG = javaClass.name
    private lateinit var sharedPreferences: SharedPreferences
    private const val PREFS_NAME = "MyHappyBotPrefs"
    private const val KEY_MODEL_DIR = "model_uri"
    private const val KEY_MODEL_NAME = "model_name"
    private const val KEY_HAS_PROMPTED_FOR_FILE = "has_prompted_for_file"
    private const val KEY_API_ENDPOINT = "api_endpoint"
    private const val KEY_API_KEY = "api_key"

    fun initialize(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            Log.d(
                TAG,
                "PreferencesManager initialized with context: ${context.javaClass.simpleName}"
            )
            forceSync()
        }
    }

    var modelUri: String?
        get() = sharedPreferences.getString(KEY_MODEL_DIR, null)
        set(value) = sharedPreferences.edit { putString(KEY_MODEL_DIR, value) }

    var modelPath: String?
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

    var apiEndpoint: String?
        get() = sharedPreferences.getString(KEY_API_ENDPOINT, null)
        set(value) = sharedPreferences.edit { putString(KEY_API_ENDPOINT, value) }

    var apiKey: String?
        get() = sharedPreferences.getString(KEY_API_KEY, null)
        set(value) = sharedPreferences.edit { putString(KEY_API_KEY, value) }
}

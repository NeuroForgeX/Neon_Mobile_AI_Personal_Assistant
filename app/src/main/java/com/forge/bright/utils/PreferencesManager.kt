package com.forge.bright.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.forge.bright.db.o.Model

private const val TAG = "PreferencesManager.kt"
private const val PREFS_NAME = "${TAG}_4_MyHappyBot"
private const val KEY_MODEL_INTERNAL_FILE_PATH = "model_internal_file_path"
private const val KEY_MODEL_NAME = "model_name"
private const val KEY_HAS_PROMPTED_FOR_FILE = "has_prompted_for_file"
private const val KEY_API_ENDPOINT = "api_endpoint"
private const val KEY_API_KEY = "api_key"

object PreferencesManager {
    private lateinit var sharedPreferences: SharedPreferences

    fun initialize(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            Log.d(TAG, "PreferencesManager initialized with context: ${context.javaClass.simpleName}")
            forceSync()
        }
    }

    var modelInternalFilePath: String?
        get() = sharedPreferences.getString(KEY_MODEL_INTERNAL_FILE_PATH, null)
        set(value) = sharedPreferences.edit { putString(KEY_MODEL_INTERNAL_FILE_PATH, value) }

    fun saveModelInformation(model: Model) {
        saveModelInformation(model.name, model.localFilePath)
    }

    fun saveModelInformation(nameOfModel: String, directoryOfModels: String) {
        Log.d(TAG, "Saving model uri=$directoryOfModels")
        val editor = sharedPreferences.edit()
        editor.putString(KEY_MODEL_INTERNAL_FILE_PATH, directoryOfModels)
        editor.putString(KEY_MODEL_NAME, nameOfModel)
        editor.apply() // Asynchronous save

        // Also commit for immediate persistence (synchronous)
        val success = editor.commit()
        Log.d(TAG, "Model info saved successfully: $success")
    }

    fun forceSync() {
        // Force SharedPreferences to sync to disk
        try {
            sharedPreferences.edit().apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync preferences", e)
        }
    }

    fun hasModelConfigured(): Boolean {
        return !modelInternalFilePath.isNullOrEmpty()
    }
}

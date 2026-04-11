package com.forge.bright.ai

import android.content.Context
import android.os.Build
import android.util.Log
import com.forge.bright.utils.FileTransferHelper.getInternalCacheDir
import com.forge.bright.utils.PreferencesManager
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import java.io.File

private const val TAG = "ChatAssistant.kt"

object ChatAssistant {
    private lateinit var engine: Engine
    private lateinit var conversation: Conversation
    private var loaded = false

    fun isLoaded(): Boolean = loaded
    fun chat(message: String): String {
        if (!loaded || !::conversation.isInitialized) {
            throw IllegalStateException("$TAG not loaded. Call @${TAG}.load() first.")
        }
        val result = conversation.sendMessage(text = message)
        return result.contents.toString()
    }

    fun load(context: Context, path: String) {
        if (loaded && ::engine.isInitialized) {
            return
        }
        try {
            // Load model from file path instead of asset
            val modelFile = File(path)
            if (!modelFile.exists()) {
                throw IllegalArgumentException("Model file not found: $path")
            }

            val engineConfig = EngineConfig(modelPath = path,
                                            backend = Backend.GPU(),
                                            cacheDir = getInternalCacheDir(context).path)
            engine = Engine(engineConfig)
            engine.initialize()
            conversation = engine.createConversation()
            loaded = true
            Log.i(TAG, "Chat model loaded successfully from: $path")
        } catch (e: Exception) {
            Log.d(TAG, "Error loading chat model", e)
        }
    }

    fun destroy() {
        if (!loaded) {
            return
        }
        try {
            if (::conversation.isInitialized) {
                conversation.close()
            }
            if (::engine.isInitialized) {
                engine.close()
            }
            loaded = false
            Log.d(TAG, "ChatAssistant resources cleaned up successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error during cleanup", e)
        }
    }

    fun load(context: Context, preferences: PreferencesManager) {
        preferences.modelInternalFilePath?.let { filepath ->
            load(context, filepath)
        }
    }
}

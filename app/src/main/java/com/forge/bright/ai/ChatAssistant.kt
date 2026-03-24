package com.forge.bright.ai

import android.content.Context
import android.util.Log
import com.github.tjake.jlama.safetensors.DType
import dev.langchain4j.memory.chat.MessageWindowChatMemory.withMaxMessages
import dev.langchain4j.model.jlama.JlamaChatModel
import dev.langchain4j.service.AiServices
import dev.langchain4j.service.SystemMessage
import java.io.File

class ChatAssistant {
    private val TAG = javaClass.simpleName
    private var assistant: Assistant? = null
    private var loaded = false

    fun chat(message: String): String {
        assistant?.let {
            return it.chat(message)
        }
        return ""
    }

    fun isLoaded(): Boolean {
        return loaded
    }

    fun loadChatModel(context: Context, path: String) {

        try {
            val modelFile = File(path)
            val modelsDir = modelFile.parentFile!!.parentFile!!.toPath()

            Log.d(
                TAG,
                "Models directory: $modelsDir and model file path ${modelFile.absolutePath}"
            )
            val model = JlamaChatModel.builder()
                .modelName("ibm-granite/granite-3.0-2b-instruct")
                .temperature(0.7f)
                .workingQuantizedType(DType.Q5)
                .workingDirectory(context.cacheDir.toPath())
                .modelCachePath(modelsDir)
                .quantizeModelAtRuntime(true)
                .build()

            Log.d(TAG, "Chat model loading....")
            assistant = AiServices.builder(Assistant::class.java)
                .chatModel(model)
                .chatMemory(withMaxMessages(50)) // Add conversation memory
                .build()
            loaded = true
            Log.d(TAG, "Chat model loaded successfully")
        } catch (e: Exception) {
            println("Error loading chat model: ${e.message}")
        }

    }
}

interface Assistant {
    @SystemMessage("You are a helpful assistant that provides concise answers.")
    fun chat(message: String): String
}

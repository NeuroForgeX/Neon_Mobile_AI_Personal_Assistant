package com.forge.bright.ai

import android.content.Context
import android.util.Log
import com.forge.bright.utils.FileTransferHelper.getInternalCacheDir
import com.forge.bright.utils.PreferencesManager
import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.Accelerator.CPU
import com.google.ai.edge.litert.Accelerator.GPU
import com.google.ai.edge.litert.Accelerator.NPU
import com.google.ai.edge.litert.BuiltinNpuAcceleratorProvider
import com.google.ai.edge.litert.CompiledModel
import com.google.ai.edge.litert.Environment
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import java.io.File

object ChatAssistant {
    private val TAG = javaClass.simpleName
    private lateinit var engine: Engine
    private lateinit var conversation: Conversation
    private var loaded = false


    fun chat(message: String): String {
        val result = conversation.sendMessage(text = message)
        return result.contents.toString()
    }

    fun isLoaded(): Boolean {
        return loaded
    }

    private fun initializeNpuModel(context: Context, modelPath: String) {
        val env = Environment.create(BuiltinNpuAcceleratorProvider(context))
        // 2. Define your fallback preferences in Options
        val options = CompiledModel.Options(
            setOf(
                NPU,
                GPU,
                CPU
            )
        )
        val compiledModel = CompiledModel.create(
            assetManager = context.assets,
            assetName = modelPath,
            options = options, optionalEnv = env
        )

    }

    fun load(context: Context, path: String) {
        if (loaded) {
            return
        }
        try {
            val modelFile = File(path)
            val modelsDir = modelFile.parentFile!!.parentFile!!.toPath()

            val model =
                CompiledModel.create(
                    assetManager = context.assets,
                    assetName = path,
                    options = CompiledModel.Options(NPU, GPU)
                )


            val engineConfig = EngineConfig(
                modelPath = path,
                backend = Backend.NPU(nativeLibraryDir = context.applicationInfo.nativeLibraryDir),
                visionBackend = Backend.GPU(),
                audioBackend = Backend.GPU(),
                cacheDir = getInternalCacheDir(context).path
            )
            engine = Engine(engineConfig)
            engine.initialize()
            conversation = engine.createConversation()

            Log.d(
                TAG,
                "Models directory: $modelsDir and model file path ${modelFile.absolutePath}"
            )

            Log.d(TAG, "Chat model loading placeholder - dependencies removed")
            loaded = true
            Log.d(TAG, "Chat model placeholder loaded successfully")
        } catch (e: Exception) {
            println("Error loading chat model: ${e.message}")
        }
    }

    fun destroy() {
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
            Log.e(TAG, "Error during cleanup: ${e.message}")
        }
    }


    fun load(context: Context, preferences: PreferencesManager) {
        val modelUri = preferences.modelUri
        modelUri?.let { filepath ->
            load(context, filepath)
        }
    }
}

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
import java.io.FileInputStream
import java.io.FileOutputStream

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

    fun copyJNILibs(context: Context) {
        try {
            val cacheDir = getInternalCacheDir(context)
            val npuRuntimeDir = File(cacheDir, "litert_npu_runtime_libraries")
            
            // Create base directory
            if (!npuRuntimeDir.exists()) {
                npuRuntimeDir.mkdirs()
            }
            
            val soc = Build.SOC_MODEL.uppercase()
            val hardware = Build.HARDWARE.lowercase()
            val sourceLibDir = File(context.applicationInfo.nativeLibraryDir)
            
            when {
                // --- Qualcomm Snapdragon Path ---
                hardware.contains("qcom") || soc.startsWith("SM") -> {
                    val runtimeVersion = when {
                        soc.contains("8650") -> "qualcomm_runtime_v81"
                        soc.contains("8550") -> "qualcomm_runtime_v75"
                        soc.contains("8450") -> "qualcomm_runtime_v73"
                        else -> "qualcomm_runtime_v69"
                    }
                    copyQualcommLibraries(sourceLibDir, npuRuntimeDir, runtimeVersion)
                }
                
                // --- MediaTek Dimensity Path ---
                hardware.contains("mt") || soc.startsWith("MT") -> {
                    copyMediaTekLibraries(sourceLibDir, npuRuntimeDir)
                }
                
                // --- Google Tensor Path ---
                hardware.contains("tensor") || hardware.contains("gs") -> {
                    copyGoogleTensorLibraries(sourceLibDir, npuRuntimeDir)
                }
                
                // --- Default case - no NPU libraries needed ---
                else -> {
                    Log.d(TAG, "No specific NPU runtime libraries needed for this hardware")
                }
            }
            
            Log.d(TAG, "JNI libraries copied successfully to ${npuRuntimeDir.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Error copying JNI libraries", e)
            throw e
        }
    }
    
    private fun copyQualcommLibraries(sourceDir: File, targetBaseDir: File, runtimeVersion: String) {
        val targetDir = File(targetBaseDir, "$runtimeVersion/src/main/jni/arm64-v8a")
        targetDir.mkdirs()
        
        // Get all .so files from source directory
        val sourceFiles = sourceDir.listFiles { file -> file.name.endsWith(".so") } ?: emptyArray()
        
        // Common Qualcomm libraries
        val commonQualcommLibs = listOf("libLiteRtDispatch_Qualcomm.so", "libQnnHtp.so", "libQnnSystem.so")
        
        // Version-specific libraries based on filename patterns
        sourceFiles.forEach { sourceFile ->
            val fileName = sourceFile.name
            when {
                // Common Qualcomm libraries
                fileName in commonQualcommLibs -> {
                    copyLibrary(sourceFile, targetDir)
                }
                // Version-specific Qualcomm libraries - detect by filename pattern
                fileName.startsWith("libQnnHtpV") && fileName.endsWith("Skel.so") -> {
                    when {
                        fileName.contains("V81") && runtimeVersion == "qualcomm_runtime_v81" -> copyLibrary(sourceFile, targetDir)
                        fileName.contains("V75") && runtimeVersion == "qualcomm_runtime_v75" -> copyLibrary(sourceFile, targetDir)
                        fileName.contains("V73") && runtimeVersion == "qualcomm_runtime_v73" -> copyLibrary(sourceFile, targetDir)
                        fileName.contains("V69") && runtimeVersion == "qualcomm_runtime_v69" -> copyLibrary(sourceFile, targetDir)
                    }
                }
                // Version-specific Qualcomm libraries - detect by filename pattern
                fileName.startsWith("libQnnHtpV") && fileName.endsWith("Stub.so") -> {
                    when {
                        fileName.contains("V81") && runtimeVersion == "qualcomm_runtime_v81" -> copyLibrary(sourceFile, targetDir)
                        fileName.contains("V75") && runtimeVersion == "qualcomm_runtime_v75" -> copyLibrary(sourceFile, targetDir)
                        fileName.contains("V73") && runtimeVersion == "qualcomm_runtime_v73" -> copyLibrary(sourceFile, targetDir)
                        fileName.contains("V69") && runtimeVersion == "qualcomm_runtime_v69" -> copyLibrary(sourceFile, targetDir)
                    }
                }
            }
        }
    }
    
    private fun copyMediaTekLibraries(sourceDir: File, targetBaseDir: File) {
        val targetDir = File(targetBaseDir, "mediatek_runtime/src/main/jni/arm64-v8a")
        targetDir.mkdirs()
        
        // Get all .so files from source directory
        val sourceFiles = sourceDir.listFiles { file -> file.name.endsWith(".so") } ?: emptyArray()
        
        sourceFiles.forEach { sourceFile ->
            val fileName = sourceFile.name
            // Copy MediaTek libraries based on filename pattern
            if (fileName == "libLiteRtDispatch_MediaTek.so") {
                copyLibrary(sourceFile, targetDir)
            }
        }
    }
    
    private fun copyGoogleTensorLibraries(sourceDir: File, targetBaseDir: File) {
        val targetDir = File(targetBaseDir, "google_tensor_runtime/src/main/jni/arm64-v8a")
        targetDir.mkdirs()
        
        // Get all .so files from source directory
        val sourceFiles = sourceDir.listFiles { file -> file.name.endsWith(".so") } ?: emptyArray()
        
        sourceFiles.forEach { sourceFile ->
            val fileName = sourceFile.name
            // Copy Google Tensor libraries based on filename pattern
            if (fileName == "libLiteRtDispatch_GoogleTensor.so") {
                copyLibrary(sourceFile, targetDir)
            }
        }
    }
    
    private fun copyLibrary(sourceFile: File, targetDir: File) {
        val targetFile = File(targetDir, sourceFile.name)
        sourceFile.copyTo(targetFile, overwrite = true)
        Log.d(TAG, "Copied ${sourceFile.name} to ${targetFile.absolutePath}")
    }


    fun getBestBackend(context: Context): Backend {
        val soc = Build.SOC_MODEL.uppercase() // e.g., "SM8550" or "MT6877"
        val hardware = Build.HARDWARE.lowercase()
        val cacheDir = getInternalCacheDir(context)
        val npuRuntimeDir = File(cacheDir, "litert_npu_runtime_libraries")

        return when {
            // --- Qualcomm Snapdragon Path ---
            hardware.contains("qcom") || soc.startsWith("SM") -> {
                // Map Qualcomm SoC to the specific runtime version in your folder structure
                // v81: Gen 3 | v75: Gen 2 | v73: Gen 1 | v69: 888/870
                val runtimeVersion = when {
                    soc.contains("8650") -> "qualcomm_runtime_v81"
                    soc.contains("8550") -> "qualcomm_runtime_v75"
                    soc.contains("8450") -> "qualcomm_runtime_v73"
                    else -> "qualcomm_runtime_v69"
                }
                val libDir = File(npuRuntimeDir, "$runtimeVersion/src/main/jni/arm64-v8a")
                Backend.NPU(libDir.absolutePath)
            }

            // --- MediaTek Dimensity Path (Moto Edge 40 Neo) ---
            hardware.contains("mt") || soc.startsWith("MT") -> {
                val libDir = File(npuRuntimeDir, "mediatek_runtime/src/main/jni/arm64-v8a")
                Backend.NPU(libDir.absolutePath) // Uses mediatek_runtime
            }

            // --- Google Tensor Path (Pixel) ---
            hardware.contains("tensor") || hardware.contains("gs") -> {
                val libDir = File(npuRuntimeDir, "google_tensor_runtime/src/main/jni/arm64-v8a")
                Backend.NPU(libDir.absolutePath) // Uses google_tensor_runtime
            }

            // --- All Other GPUs (Mali, Adreno fallback) ---
            else -> Backend.GPU()
        }
    }


    fun load(context: Context, path: String) {
        if (loaded && ::engine.isInitialized) {
            return
        }
        try {
            // Copy JNI libraries to cache directory first
            copyJNILibs(context)
            
            // Load model from file path instead of asset
            val modelFile = File(path)
            if (!modelFile.exists()) {
                throw IllegalArgumentException("Model file not found: $path")
            }

            val engineConfig = EngineConfig(modelPath = path,
                                            backend = getBestBackend(context),
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

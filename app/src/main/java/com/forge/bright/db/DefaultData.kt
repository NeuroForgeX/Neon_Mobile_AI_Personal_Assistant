package com.forge.bright.db

import android.content.Context
import android.util.Log
import com.forge.bright.R
import com.forge.bright.db.o.Model
import com.forge.bright.db.o.ModelType.GGUF
import com.forge.bright.db.o.ModelType.LiteRTLM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

private const val TAG = "DefaultData.kt"

object DefaultData {
    /**
     * Check if models table exists and has data, populate from JSON if empty
     */
    suspend fun initializeModelsIfEmpty(context: Context) {
        val database = ConversationDatabase.getDatabase(context)
        withContext(Dispatchers.IO) {
            try {
                // Check if models table has any data
                val modelCount = database.modelDao().getModelCount()
                if (modelCount == 0) {
                    // Table is empty, populate from JSON
                    populateModelsFromJson(context, database)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error populating models: ${e.message}")
            }
        }
    }

    private suspend fun populateModelsFromJson(context: Context, database: ConversationDatabase) {
        try {
            // Read JSON from raw resources
            val jsonString = context.resources.openRawResource(R.raw.models).bufferedReader().use { it.readText() }

            val jsonObject = JSONObject(jsonString)
            val models = mutableListOf<Model>()

            // Parse liteRTLM models
            val liteRTLM = jsonObject.optJSONObject("liteRTLM")
            if (liteRTLM != null) {
                liteRTLM.keys().forEach { key ->
                    val modelJson = liteRTLM.getJSONObject(key)
                    models.add(Model(name = modelJson.optString("name", key),
                                     shortName = modelJson.optString("shortName", key),
                                     description = modelJson.optString("description", ""),
                                     size = modelJson.optString("size", ""),
                                     downloadUrl = modelJson.optString("downloadUrl", ""),
                                     owner = modelJson.optString("owner", ""),
                                     type = LiteRTLM))
                }
            }

            // Parse gguf models
            val gguf = jsonObject.optJSONObject("gguf")
            if (gguf != null) {
                gguf.keys().forEach { key ->
                    val modelJson = gguf.getJSONObject(key)
                    models.add(Model(name = modelJson.optString("name", key),
                                     shortName = modelJson.optString("shortName", key),
                                     description = modelJson.optString("description", ""),
                                     size = modelJson.optString("size", ""),
                                     downloadUrl = modelJson.optString("downloadUrl", ""),
                                     owner = modelJson.optString("owner", ""),
                                     type = GGUF))
                }
            }

            // Insert all models into database
            if (models.isNotEmpty()) {
                database.modelDao().insertModels(models)
            }

        } catch (e: Exception) {
            Log.d(TAG, "", e)
        }
    }
}


package com.forge.bright.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.forge.bright.R
import com.forge.bright.db.DataAccess
import com.forge.bright.db.o.Model
import com.forge.bright.db.o.ModelType.GGUF
import com.forge.bright.db.o.ModelType.LiteRTLM
import com.forge.bright.db.o.ModelUIStatus
import com.forge.bright.db.o.getModelStatus
import com.forge.bright.ui.components.DownloadingModelItem
import com.forge.bright.ui.components.OfflineAvailableModelItem
import com.forge.bright.ui.components.UnAvailableModelItem
import com.forge.bright.utils.PreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSetupScreen(navController: NavHostController) {
    val context = LocalContext.current
    val models by DataAccess.getAllModels().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        PreferencesManager.initialize(context)
        DataAccess.initialize(context)
    }

    ModelSetupScreenContent(models = models, onNavigateToChat = {
        navController.navigate("chat") {
            popUpTo("model_setup") { inclusive = true }
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSetupScreenContent(
    models: List<Model>, onNavigateToChat: () -> Unit
                           ) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        PreferencesManager.initialize(context)
    }

    // Helper function to get appropriate image resource based on model type
    fun getModelImageRes(model: Model): Int {
        return when (model.type) {
            GGUF -> R.drawable.cloud_download_svgrepo_com
            LiteRTLM -> R.drawable.baseline_attach_email_24
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Fixed header
        Box(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), contentAlignment = Alignment.Center) {
            Text(text = "Select/Download Model to load", fontSize = 20.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }

        // Scrollable content area
        Box(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5)) // Very light black color
                .verticalScroll(rememberScrollState())) {
            Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Spacer(modifier = Modifier.height(16.dp))

                // Dynamically generate model items from database
                models.forEach { model ->
                    val status = model.getModelStatus()
                    val imageRes = getModelImageRes(model)

                    when (status) {
                        ModelUIStatus.OFFLINE_AVAILABLE -> {
                            OfflineAvailableModelItem(title = model.name, description = model.description, imageRes = imageRes, onClick = {
                                // Handle offline model selection
                            })
                        }

                        ModelUIStatus.UNAVAILABLE -> {
                            UnAvailableModelItem(title = model.name, description = model.description, imageRes = imageRes, onClick = {
                                // Handle unavailable model selection (start download)
                            })
                        }

                        ModelUIStatus.DOWNLOADING -> {
                            DownloadingModelItem(title = model.name, description = model.description, imageRes = imageRes, downloadProgress = 0f, // TODO: Get from download progress table
                                                 onClick = {
                                                     // Handle downloading model interaction
                                                 })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Fixed bottom button
        Box(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
            Button(onClick = onNavigateToChat, modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)) {
                Text(text = "Continue to Chat", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModelSetupScreenContentPreview() {
    MaterialTheme {
        // Create sample models for preview
        val sampleModels = listOf(Model(name = "Phi-3 Mini",
                                        shortName = "phi3-mini",
                                        description = "Lightweight language model for mobile devices",
                                        size = "2.2GB",
                                        owner = "Microsoft",
                                        localUri = Uri.parse("file:///models/phi3-mini.gguf"),
                                        type = com.forge.bright.db.o.ModelType.GGUF),
                                  Model(name = "GPT-4",
                                        shortName = "gpt4",
                                        description = "Large language model requiring download",
                                        size = "13GB",
                                        downloadUrl = "https://example.com/gpt4.gguf",
                                        owner = "OpenAI",
                                        type = com.forge.bright.db.o.ModelType.GGUF))
        ModelSetupScreenContent(models = sampleModels, onNavigateToChat = {})
    }
}

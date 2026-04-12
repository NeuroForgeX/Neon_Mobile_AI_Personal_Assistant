package com.forge.bright.ui.screens

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.forge.bright.db.DataAccess
import com.forge.bright.db.o.Model
import com.forge.bright.db.o.ModelType.GGUF
import com.forge.bright.db.o.isOfflineAvailable
import com.forge.bright.db.o.isOfflineDownloaded
import com.forge.bright.ui.components.DebugLocalModelItem
import com.forge.bright.ui.components.OfflineAvailableModelItem
import com.forge.bright.ui.components.OnlineModelSelectionItem
import com.forge.bright.ui.theme.lightBlack

private const val TAG = "ModelSetupScreen.kt"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSetupScreen(navController: NavHostController) {
    val models by DataAccess.getAllModels().collectAsState(initial = emptyList())
    ModelSetupScreenContent(models = models, onNavigateToChat = {
        navController.navigate("chat") {
            popUpTo("model_setup") { inclusive = true }
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSetupScreenContent(models: List<Model>, onNavigateToChat: () -> Unit) {
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
                .background(lightBlack) // Very light black color
                .verticalScroll(rememberScrollState())) {
            Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Spacer(modifier = Modifier.height(16.dp))

                for (model in models) {
                    if (model.isOfflineDownloaded()) {
                        DebugLocalModelItem(model)
                        continue
                    }
                    if (model.isOfflineAvailable()) {
                        OfflineAvailableModelItem(model)
                        continue
                    }
                    OnlineModelSelectionItem(model = model)
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
                Text(text = "Load & Continue", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModelSetupScreenContentPreview() {
    MaterialTheme {
        // Create sample models for preview
        val sampleModels = listOf(Model(name = "Phi-3 Mini.gguf",
                                        shortName = "phi3-mini",
                                        description = "Lightweight language model for mobile devices",
                                        size = "2.2GB",
                                        owner = "Microsoft",
                                        localFilePath = "file:///models/phi3-mini.gguf",
                                        type = GGUF),
                                  Model(name = "GPT-4.gguf", shortName = "gpt4", description = "Large language model requiring download", size = "13GB", owner = "OpenAI", type = GGUF))
        ModelSetupScreenContent(models = sampleModels, onNavigateToChat = {})
    }
}
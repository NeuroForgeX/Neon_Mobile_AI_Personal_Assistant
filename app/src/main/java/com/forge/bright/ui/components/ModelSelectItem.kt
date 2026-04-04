package com.forge.bright.ui.components

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.forge.bright.AVAILABLE_LOCAL
import com.forge.bright.DOWNLOAD_ICON_DESC
import com.forge.bright.DOWNLOAD_NOW
import com.forge.bright.DOWNLOAD_PROGRESS_COMPLETED
import com.forge.bright.R
import com.forge.bright.ai.ChatAssistant.destroy
import com.forge.bright.ai.ChatAssistant.load
import com.forge.bright.db.DataAccess.updateModel
import com.forge.bright.db.o.Model
import com.forge.bright.db.o.ModelType.GGUF
import com.forge.bright.db.o.buildLocalFileDirectory
import com.forge.bright.db.o.buildLocalFilePath
import com.forge.bright.utils.FileTransferHelper
import com.forge.bright.utils.PreferencesManager
import com.forge.bright.utils.PreferencesManager.saveModelInformation
import com.ketch.Ketch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "ModelSelectItem.kt"

@Composable
fun OfflineAvailableModelItem(model: Model) {
    val context = LocalContext.current
    Card(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
         shape = RoundedCornerShape(12.dp),
         elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
         onClick = {
             // Handle offline model selection
             destroy()
             saveModelInformation(model)
             load(context, PreferencesManager)
         }) {
        Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Image
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(id = R.drawable.cloud_download_svgrepo_com),
                      contentDescription = model.shortName,
                      modifier = Modifier
                              .fillMaxSize()
                              .clip(RoundedCornerShape(8.dp)),
                      contentScale = ContentScale.Fit)
            }
            // Text content and status
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = model.shortName, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = model.description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                Text(text = AVAILABLE_LOCAL, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

@Composable
fun OnlineModelSelectionItem(model: Model) {
    val isPreview = LocalInspectionMode.current
    val ketch = if (isPreview) null else Ketch.builder().enableLogs(true).build(LocalContext.current)
    var progress by remember { mutableIntStateOf(0) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(STARTED) {
            // Observes every download currently managed by Ketch
            ketch!!.observeDownloadByTag(model.name).flowOn(Dispatchers.IO).collect { downloads ->
                if (downloads.isNotEmpty()) {
                    downloads.first().let {
                        progress = it.progress
                    }
                }
            }
        }

    }
    if (progress == 0) {
        OnlineModelItem(model, ketch)
        return
    }

    if (progress == 100) {
        OfflineAvailableModelItem(model)
        return
    }

    DownloadingModelItem(model, progress)

}

@Composable
fun OnlineModelItem(model: Model, ketch: Ketch?) {
    val context = LocalContext.current
    var click by remember { mutableStateOf(false) }
    Card(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
         shape = RoundedCornerShape(12.dp),
         elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
         onClick = {
             if (!click) {
                 click = true
                 ketch!!.download(url = model.downloadUrl, path = model.buildLocalFileDirectory(context, FileTransferHelper), fileName = model.name, tag = model.name, supportPauseResume = true)
             }
         }) {
        Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Image
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(id = R.drawable.cloud_download_sign_svgrepo_com),
                      contentDescription = DOWNLOAD_ICON_DESC,
                      modifier = Modifier
                              .fillMaxSize()
                              .clip(RoundedCornerShape(8.dp)),
                      contentScale = ContentScale.Fit)
            }
            // Text content and status
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = model.shortName, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = model.description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                Text(text = DOWNLOAD_NOW, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

@Composable
fun DownloadingModelItem(model: Model, progress: Int) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Card(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
         shape = RoundedCornerShape(12.dp),
         elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
         onClick = {
             destroy()
             val savedFilePath = model.buildLocalFilePath(context, FileTransferHelper)
             val updatedModel = model.copy(localFilePath = savedFilePath)
             saveModelInformation(updatedModel)
             saveUpdatedModelToDB(coroutineScope, updatedModel)
             load(context, PreferencesManager)
         }) {
        Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Progress indicator
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            }
            // Text content and progress
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = model.shortName, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = model.description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    LinearProgressIndicator(progress = { progress / 100f }, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.primary)
                    Text(text = DOWNLOAD_PROGRESS_COMPLETED.format(progress), fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

private fun saveUpdatedModelToDB(coroutineScope: CoroutineScope, model: Model) {
    coroutineScope.launch {
        try {
            updateModel(model)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update model in database", e)
        }
    }
}

@Composable
fun DebugLocalModelItem(model: Model) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val filePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            Log.d(TAG, "Selected file: $it")

            // Get the original filename from the URI
            val fileName = context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                cursor.moveToFirst()
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                cursor.getString(nameIndex)
            } ?: "model.litertlm"

            // Create proper file path in directory
            val directory = model.buildLocalFileDirectory(context, FileTransferHelper)
            val targetFile = File(directory, fileName)

            // Copy the selected file to local storage
            val copiedFile = FileTransferHelper.copyFileToInternal(context, it, targetFile)
            copiedFile?.let { filePath ->
                Toast.makeText(context, "File copied successfully!", Toast.LENGTH_LONG).show()
                Log.d(TAG, "Copied file: $filePath")
                destroy()
                val updatedModel = model.copy(localFilePath = filePath)
                saveModelInformation(updatedModel)
                saveUpdatedModelToDB(coroutineScope, updatedModel)
                load(context, PreferencesManager)
                Toast.makeText(context, "LiteRTLM model loaded successfully!", Toast.LENGTH_LONG).show()
            } ?: run {
                Toast.makeText(context, "Failed to copy file", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Storage permission launcher for Android 12 and below
    val storagePermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // Permission granted, launch file picker
            filePickerLauncher.launch("application/octet-stream")
        } else {
            Log.d(TAG, "Storage permission denied")
        }
    }

    Card(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
         shape = RoundedCornerShape(12.dp),
         elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
         colors = CardDefaults.cardColors(containerColor = Color(0xFF90EE90)), // Light green
         onClick = {
             filePickerLauncher.launch("application/octet-stream")
         }) {
        Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Image
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(id = R.drawable.cloud_download_svgrepo_com),
                      contentDescription = model.shortName,
                      modifier = Modifier
                              .fillMaxSize()
                              .clip(RoundedCornerShape(8.dp)),
                      contentScale = ContentScale.Fit)
            }
            // Text content and status
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = model.shortName, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = model.description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                Text(text = AVAILABLE_LOCAL, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OfflineAvailableModelItemPreview() {
    OfflineAvailableModelItem(Model(id = 1,
                                    name = "Phi 3k Mini.gguf",
                                    shortName = "Phi 3k Mini",
                                    description = "Lightweight language model for mobile devices",
                                    size = "2.3GB",
                                    downloadUrl = "https://example.com/llama2.gguf",
                                    owner = "Microsoft",
                                    localFilePath = "",
                                    type = GGUF))
}

@Preview(showBackground = true)
@Composable
fun OnlineModelItemPreview() {
    val sampleModel = Model(id = 1,
                            name = "Llama 2.gguf",
                            shortName = "Llama 2",
                            description = "Open source language model",
                            size = "13GB",
                            downloadUrl = "https://example.com/llama2.gguf",
                            owner = "Meta",
                            localFilePath = "",
                            type = GGUF)
    OnlineModelItem(model = sampleModel, ketch = null)
}

@Preview(showBackground = true)
@Composable
fun DownloadingModelItemPreview() {
    val sampleModel = Model(id = 1,
                            name = "Llama 2.gguf",
                            shortName = "Llama 2",
                            description = "Open source language model",
                            size = "13GB",
                            downloadUrl = "https://example.com/llama2.gguf",
                            owner = "Meta",
                            localFilePath = "",
                            type = GGUF)
    DownloadingModelItem(model = sampleModel, progress = 75)
}

@Preview(showBackground = true)
@Composable
fun DebugLocalModelItemPreview() {
    DebugLocalModelItem(Model(id = 1,
                              name = "Phi 3k Mini.gguf",
                              shortName = "Phi 3k Mini",
                              description = "Lightweight language model for mobile devices",
                              size = "2.3GB",
                              downloadUrl = "https://example.com/phi3k.gguf",
                              owner = "Microsoft",
                              localFilePath = "",
                              type = GGUF))
}

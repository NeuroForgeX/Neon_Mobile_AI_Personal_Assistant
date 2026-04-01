package com.forge.bright.ui.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OfflineAvailableModelItem(title: String,
                          description: String,
                          imageRes: Int,
                          onClick: () -> Unit) {
    Card(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
         shape = RoundedCornerShape(12.dp),
         elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
         onClick = onClick) {
        Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Image
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(id = imageRes), contentDescription = title, modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Fit)
            }
            // Text content and status
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                Text(text = "Available in local", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

@Composable
fun UnAvailableModelItem(title: String,
                       description: String,
                       imageRes: Int,
                       onClick: () -> Unit) {
    Card(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
         shape = RoundedCornerShape(12.dp),
         elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
         onClick = onClick) {
        Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Image (disabled state)
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(id = imageRes), contentDescription = title, modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Fit)
            }
            // Text content and status
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                Text(text = "Download Now...", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End))
            }
        }
    }
}

@Composable
fun DownloadingModelItem(title: String,
                       description: String,
                       imageRes: Int,
                       downloadProgress: Float = 0f,
                       onClick: () -> Unit) {
    Card(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
         shape = RoundedCornerShape(12.dp),
         elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Loading indicator
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            }
            // Text content and status
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(text = description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    LinearProgressIndicator(
                        progress = downloadProgress,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = "${(downloadProgress * 100).toInt()}% complete", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OfflineAvailableModelItemPreview() {
    OfflineAvailableModelItem(
        title = "Phi-3 Mini",
        description = "Lightweight language model for mobile devices",
        imageRes = android.R.drawable.ic_menu_camera,
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun UnAvailableModelItemPreview() {
    UnAvailableModelItem(
        title = "GPT-4",
        description = "Large language model requiring download",
        imageRes = android.R.drawable.ic_menu_camera,
        onClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun DownloadingModelItemPreview() {
    DownloadingModelItem(
        title = "Llama 2",
        description = "Open source language model",
        imageRes = android.R.drawable.ic_menu_camera,
        downloadProgress = 0.65f,
        onClick = {}
    )
}

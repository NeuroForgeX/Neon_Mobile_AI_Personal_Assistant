package com.forge.bright.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forge.bright.ABOUT_DESC
import com.forge.bright.ABOUT_TITLE
import com.forge.bright.APPEARANCE_DESC
import com.forge.bright.APPEARANCE_TITLE
import com.forge.bright.GENERAL_SETTINGS
import com.forge.bright.MODEL_CONFIGURATION_DESC
import com.forge.bright.MODEL_CONFIGURATION_TITLE
import com.forge.bright.SETTINGS_TITLE
import com.forge.bright.STORAGE_DESC
import com.forge.bright.STORAGE_TITLE

private const val TAG = "SettingsScreen.kt"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = SETTINGS_TITLE,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = GENERAL_SETTINGS,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Model Settings
                SettingsItem(
                    title = MODEL_CONFIGURATION_TITLE,
                    description = MODEL_CONFIGURATION_DESC
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Theme Settings
                SettingsItem(
                    title = APPEARANCE_TITLE,
                    description = APPEARANCE_DESC
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Storage Settings
                SettingsItem(
                    title = STORAGE_TITLE,
                    description = STORAGE_DESC
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                // About Settings
                SettingsItem(
                    title = ABOUT_TITLE,
                    description = ABOUT_DESC
                )
            }
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = description,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    com.forge.bright.ui.theme.MyHappyBotTheme {
        SettingsScreen()
    }
}


package com.forge.bright.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forge.bright.FEATURES_TITLE
import com.forge.bright.FEATURE_AI_CHAT
import com.forge.bright.FEATURE_MULTIPLE_MODELS
import com.forge.bright.FEATURE_OFFLINE
import com.forge.bright.FEATURE_SETTINGS
import com.forge.bright.WELCOME_SUBTITLE
import com.forge.bright.WELCOME_TITLE

private const val TAG = "MainScreen.kt"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(text = WELCOME_TITLE, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = WELCOME_SUBTITLE, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

        Spacer(modifier = Modifier.height(32.dp))

        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = FEATURES_TITLE, fontSize = 20.sp, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.height(8.dp))

                Text(FEATURE_AI_CHAT)
                Text(FEATURE_MULTIPLE_MODELS)
                Text(FEATURE_OFFLINE)
                Text(FEATURE_SETTINGS)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    com.forge.bright.ui.theme.MyHappyBotTheme {
        MainScreen()
    }
}


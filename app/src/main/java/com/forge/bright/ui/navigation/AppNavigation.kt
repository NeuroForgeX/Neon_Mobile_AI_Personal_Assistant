package com.forge.bright.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.forge.bright.ui.screens.ChatScreen
import com.forge.bright.ui.screens.ModelSetupScreen
import com.forge.bright.utils.PreferencesManager
import android.util.Log

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    onModelConfigCheck: (Boolean) -> Unit) {
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        val hasModelConfigured = PreferencesManager.hasModelConfigured()
        onModelConfigCheck(hasModelConfigured)
        
        if (!hasModelConfigured) {
            Log.d("AppNavigation", "No model configured, navigating to setup")
            navController.navigate("model_setup") {
                popUpTo("chat") { inclusive = true }
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = if (PreferencesManager.hasModelConfigured()) "chat" else "model_setup"
    ) {
        composable("chat") {
            ChatScreen(navController = navController)
        }
        composable("model_setup") {
            ModelSetupScreen(navController = navController)
        }
    }
}

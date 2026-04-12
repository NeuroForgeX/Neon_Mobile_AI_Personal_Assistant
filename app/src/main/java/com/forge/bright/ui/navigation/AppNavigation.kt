package com.forge.bright.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.forge.bright.NAV_CHAT
import com.forge.bright.NAV_HOME
import com.forge.bright.NAV_MODELS
import com.forge.bright.NAV_SETTINGS
import com.forge.bright.NavigationRoute
import com.forge.bright.db.DataAccess
import com.forge.bright.ui.screens.ChatScreen
import com.forge.bright.ui.screens.MainScreen
import com.forge.bright.ui.screens.ModelSetupScreen
import com.forge.bright.ui.screens.SettingsScreen
import com.forge.bright.ui.theme.MyHappyBotTheme
import com.forge.bright.utils.PreferencesManager.hasModelConfigured

private const val TAG = "AppNavigation.kt"

data class BottomNavItem(val title: String, val icon: ImageVector, val route: String)

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController(), onModelConfigCheck: (Boolean) -> Unit) {
    LocalContext.current

    LaunchedEffect(Unit) {
        val hasModelConfigured = hasModelConfigured()
        onModelConfigCheck(hasModelConfigured)

        if (!hasModelConfigured) {
            Log.d(TAG, "No model configured, navigating to setup")
            navController.navigate(NavigationRoute.MODEL_SETUP.route) {
                popUpTo(NavigationRoute.MAIN.route) { inclusive = false }
            }
        }
    }

    // Start destination is always main screen
    val startDestination = NavigationRoute.MAIN.route

    val bottomNavItems = listOf(BottomNavItem(NAV_HOME, Icons.Filled.Home, NavigationRoute.MAIN.route),
                                BottomNavItem(NAV_CHAT, Icons.Filled.Chat, NavigationRoute.CHAT.route),
                                BottomNavItem(NAV_MODELS, Icons.Filled.Download, NavigationRoute.MODEL_SETUP.route),
                                BottomNavItem(NAV_SETTINGS, Icons.Filled.Settings, NavigationRoute.SETTINGS.route))

    Scaffold(bottomBar = {
        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            bottomNavItems.forEach { item ->
                NavigationBarItem(icon = { Icon(item.icon, contentDescription = item.title) }, label = { Text(item.title) }, selected = currentRoute == item.route, onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
            }
        }
    }) { paddingValues ->
        NavHost(navController = navController, startDestination = startDestination, modifier = Modifier.padding(paddingValues)) {
            composable(NavigationRoute.MAIN.route) {
                MainScreen()
            }
            composable(NavigationRoute.CHAT.route) {
                ChatScreen(navController = navController)
            }
            composable(NavigationRoute.MODEL_SETUP.route) {
                ModelSetupScreen(navController = navController)
            }
            composable(NavigationRoute.SETTINGS.route) {
                SettingsScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    
    // Initialize navigation titles for preview
    NAV_HOME = "Home"
    NAV_CHAT = "Chat"
    NAV_MODELS = "Models"
    NAV_SETTINGS = "Settings"

    val isPreview = LocalInspectionMode.current
    MyHappyBotTheme {
        if (isPreview) {
            DataAccess.initializeDebug()
        }
        AppNavigation(navController = rememberNavController(), onModelConfigCheck = { _ -> })
    }
}

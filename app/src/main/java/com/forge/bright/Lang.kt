package com.forge.bright

import android.content.Context

var APP_NAME: String = ""

// Navigation
var NAV_HOME: String = ""
var NAV_CHAT: String = ""
var NAV_MODELS: String = ""
var NAV_SETTINGS: String = ""

// Main Screen
var WELCOME_TITLE: String = ""
var WELCOME_SUBTITLE: String = ""
var FEATURES_TITLE: String = ""
var FEATURE_AI_CHAT: String = ""
var FEATURE_MULTIPLE_MODELS: String = ""
var FEATURE_OFFLINE: String = ""
var FEATURE_SETTINGS: String = ""

// Chat Screen
var TYPE_MESSAGE_HINT: String = ""
var SEND_MESSAGE_DESC: String = ""
var SENDING_MESSAGE: String = ""
var ERROR_AI_RESPONSE: String = ""

// Model Setup
var SETUP_AI_MODELS: String = ""
var DOWNLOAD_PHI_3_MODEL: String = ""
var SELECT_LOCAL_GGUF_FILE: String = ""
var GO_TO_CHAT: String = ""
var SELECT_AN_OPTION_TO_GET_STARTED: String = ""

// Model Items
var AVAILABLE_LOCAL: String = ""
var DOWNLOAD_NOW: String = ""
var DOWNLOAD_ICON_DESC: String = ""
var DOWNLOAD_PROGRESS_COMPLETED: String = ""

// Chat Messages
var SEEN: String = ""
var TYPING_INDICATOR: String = ""

// Settings Screen
var SETTINGS_TITLE: String = ""
var GENERAL_SETTINGS: String = ""
var MODEL_CONFIGURATION_TITLE: String = ""
var MODEL_CONFIGURATION_DESC: String = ""
var APPEARANCE_TITLE: String = ""
var APPEARANCE_DESC: String = ""
var STORAGE_TITLE: String = ""
var STORAGE_DESC: String = ""
var ABOUT_TITLE: String = ""
var ABOUT_DESC: String = ""

// Date/Time
var NOW: String = ""
var TODAY: String = ""
var YESTERDAY: String = ""

// Permissions
var STORAGE_PERMISSIONS_REQUIRED: String = ""
var ALL_PERMISSIONS_GRANTED: String = ""
var PERMISSION_GRANTED: String = ""
var PERMISSION_DENIED: String = ""

// App Utils
var MY_HAPPY_BOT_FOLDER: String = ""
var HELLO_FILE_CONTENT: String = ""
var STORAGE_PERMISSIONS_NOT_GRANTED: String = ""
var FOLDER_CREATED_SUCCESS: String = ""
var FOLDER_ALREADY_EXISTS: String = ""
var FAILED_CREATE_FOLDER: String = ""

// Database
var CONVERSATION_DATABASE_NAME: String = ""

// Splash Screen
var TITLE_ACTIVITY_SPLASH_SCREEN: String = ""

// Navigation Routes
var ROUTE_MAIN: String = ""
var ROUTE_CHAT: String = ""
var ROUTE_MODEL_SETUP: String = ""
var ROUTE_SETTINGS: String = ""

// Log Tags
var LOG_MAIN_ACTIVITY: String = ""
var LOG_APP_NAVIGATION: String = ""
var LOG_APP_UTILS: String = ""

fun loadStringResources(context: Context) {
    // App Info
    APP_NAME = context.getString(R.string.app_name)

    // Navigation
    NAV_HOME = context.getString(R.string.nav_home)
    NAV_CHAT = context.getString(R.string.nav_chat)
    NAV_MODELS = context.getString(R.string.nav_models)
    NAV_SETTINGS = context.getString(R.string.nav_settings)

    // Main Screen
    WELCOME_TITLE = context.getString(R.string.welcome_title)
    WELCOME_SUBTITLE = context.getString(R.string.welcome_subtitle)
    FEATURES_TITLE = context.getString(R.string.features_title)
    FEATURE_AI_CHAT = context.getString(R.string.feature_ai_chat)
    FEATURE_MULTIPLE_MODELS = context.getString(R.string.feature_multiple_models)
    FEATURE_OFFLINE = context.getString(R.string.feature_offline)
    FEATURE_SETTINGS = context.getString(R.string.feature_settings)

    // Chat Screen
    TYPE_MESSAGE_HINT = context.getString(R.string.type_message_hint)
    SEND_MESSAGE_DESC = context.getString(R.string.send_message_desc)
    SENDING_MESSAGE = context.getString(R.string.sending_message)
    ERROR_AI_RESPONSE = context.getString(R.string.error_ai_response)

    // Model Setup
    SETUP_AI_MODELS = context.getString(R.string.setup_ai_models)
    DOWNLOAD_PHI_3_MODEL = context.getString(R.string.download_phi_3_model)
    SELECT_LOCAL_GGUF_FILE = context.getString(R.string.select_local_gguf_file)
    GO_TO_CHAT = context.getString(R.string.go_to_chat)
    SELECT_AN_OPTION_TO_GET_STARTED = context.getString(R.string.select_an_option_to_get_started)

    // Model Items
    AVAILABLE_LOCAL = context.getString(R.string.available_local)
    DOWNLOAD_NOW = context.getString(R.string.download_now)
    DOWNLOAD_ICON_DESC = context.getString(R.string.download_icon_desc)
    DOWNLOAD_PROGRESS_COMPLETED = context.getString(R.string.download_progress_completed)

    // Chat Messages
    SEEN = context.getString(R.string.seen)
    TYPING_INDICATOR = context.getString(R.string.typing_indicator)

    // Settings Screen
    SETTINGS_TITLE = context.getString(R.string.settings_title)
    GENERAL_SETTINGS = context.getString(R.string.general_settings)
    MODEL_CONFIGURATION_TITLE = context.getString(R.string.model_configuration_title)
    MODEL_CONFIGURATION_DESC = context.getString(R.string.model_configuration_desc)
    APPEARANCE_TITLE = context.getString(R.string.appearance_title)
    APPEARANCE_DESC = context.getString(R.string.appearance_desc)
    STORAGE_TITLE = context.getString(R.string.storage_title)
    STORAGE_DESC = context.getString(R.string.storage_desc)
    ABOUT_TITLE = context.getString(R.string.about_title)
    ABOUT_DESC = context.getString(R.string.about_desc)

    // Date/Time
    NOW = context.getString(R.string.now)
    TODAY = context.getString(R.string.today)
    YESTERDAY = context.getString(R.string.yesterday)

    // Permissions
    STORAGE_PERMISSIONS_REQUIRED = context.getString(R.string.storage_permissions_required)
    ALL_PERMISSIONS_GRANTED = context.getString(R.string.all_permissions_granted)
    PERMISSION_GRANTED = context.getString(R.string.permission_granted)
    PERMISSION_DENIED = context.getString(R.string.permission_denied)

    // App Utils
    MY_HAPPY_BOT_FOLDER = context.getString(R.string.my_happy_bot_folder)
    HELLO_FILE_CONTENT = context.getString(R.string.hello_file_content)
    STORAGE_PERMISSIONS_NOT_GRANTED = context.getString(R.string.storage_permissions_not_granted)
    FOLDER_CREATED_SUCCESS = context.getString(R.string.folder_created_success)
    FOLDER_ALREADY_EXISTS = context.getString(R.string.folder_already_exists)
    FAILED_CREATE_FOLDER = context.getString(R.string.failed_create_folder)

    // Database
    CONVERSATION_DATABASE_NAME = context.getString(R.string.conversation_database_name)

    // Splash Screen
    TITLE_ACTIVITY_SPLASH_SCREEN = context.getString(R.string.title_activity_splash_screen)

    // Navigation Routes
    ROUTE_MAIN = context.getString(R.string.route_main)
    ROUTE_CHAT = context.getString(R.string.route_chat)
    ROUTE_MODEL_SETUP = context.getString(R.string.route_model_setup)
    ROUTE_SETTINGS = context.getString(R.string.route_settings)

    // Log Tags
    LOG_MAIN_ACTIVITY = context.getString(R.string.log_main_activity)
    LOG_APP_NAVIGATION = context.getString(R.string.log_app_navigation)
    LOG_APP_UTILS = context.getString(R.string.log_app_utils)
}
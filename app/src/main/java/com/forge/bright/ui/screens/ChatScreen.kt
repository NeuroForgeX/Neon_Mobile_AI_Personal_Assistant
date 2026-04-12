package com.forge.bright.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.forge.bright.ERROR_AI_RESPONSE
import com.forge.bright.SEEN
import com.forge.bright.SEND_MESSAGE_DESC
import com.forge.bright.TYPE_MESSAGE_HINT
import com.forge.bright.TYPING_INDICATOR
import com.forge.bright.ai.ChatAssistant
import com.forge.bright.db.o.ChatMessage
import com.forge.bright.db.o.MessageType
import com.forge.bright.ui.components.ChatMessageItem
import com.forge.bright.ui.theme.MyHappyBotTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ChatScreen.kt"

fun insertChatMessage(message: ChatMessage, messages: MutableList<ChatMessage>): MutableList<ChatMessage> {
    val updatedMessages = messages.toMutableList()
    updatedMessages.add(message)
    updatedMessages.sortBy { it.timestamp }
    return updatedMessages
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavHostController) {
    LocalContext.current
    val scope = rememberCoroutineScope()

    var messages by remember { mutableStateOf(mutableListOf<ChatMessage>()) }
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new messages are added
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Messages list
        LazyColumn(state = listState, modifier = Modifier.weight(1f), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(messages) { message ->
                ChatMessageItem(message)
            }
        }

        // Input area
        Surface(shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = messageText,
                                  onValueChange = { messageText = it },
                                  modifier = Modifier.weight(1f),
                                  placeholder = { Text(TYPE_MESSAGE_HINT) },
                                  maxLines = 3,
                                  shape = RoundedCornerShape(24.dp))

                IconButton(onClick = {
                    if (messageText.trim().isNotEmpty()) {
                        val message = ChatMessage(topicId = 0, message = messageText.trim(), messageType = MessageType.FROM_USER)
                        messageText = ""
                        messages = insertChatMessage(message, messages)
                        // Get AI response
                        scope.launch {
                            try {
                                withContext(Dispatchers.Main) {
                                    // Switch to IO thread for the DB query
                                    val response = withContext(Dispatchers.Default) {
                                        ChatAssistant.chat(message.message)
                                    }
                                    messages = insertChatMessage(ChatMessage(topicId = 0, message = response, messageType = MessageType.FROM_AI), messages)
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    val errorMessageText = ERROR_AI_RESPONSE.format(e.message)
                                    val errorMessage = ChatMessage(topicId = 0, message = errorMessageText, messageType = MessageType.STATIC_ERROR_NOTIFICATION)
                                    messages = insertChatMessage(errorMessage, messages)
                                }
                            }
                        }
                    }
                }) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = SEND_MESSAGE_DESC, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    MyHappyBotTheme {
        ChatScreenContentPreview()
    }
}

@Composable
private fun ChatScreenContentPreview() {
    val typingIndicator = TYPING_INDICATOR
    val seen = SEEN
    var messages by remember {
        mutableStateOf(listOf(ChatMessage(message = "Hello! How can I help you today?", topicId = 0, messageType = MessageType.FROM_AI),
                              ChatMessage(message = "I need help with my Android app", topicId = 0, messageType = MessageType.FROM_USER),
                              ChatMessage(message = "I'd be happy to help! What specific issue are you facing?", topicId = 0, messageType = MessageType.FROM_AI),
                              ChatMessage(message = "Technical Error occurred", topicId = 0, messageType = MessageType.STATIC_ERROR_NOTIFICATION),
                              ChatMessage(message = typingIndicator, topicId = 0, messageType = MessageType.DYNAMIC_TYPING_NOTIFICATION),
                              ChatMessage(message = seen, topicId = 0, messageType = MessageType.DYNAMIC_SEEN_NOTIFICATION),
                              ChatMessage(message = "Sent", topicId = 0, messageType = MessageType.DYNAMIC_SEND_NOTIFICATION)))
    }
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(state = listState, modifier = Modifier.weight(1f), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(messages) { message ->
                ChatMessageItem(message)
            }
        }

        Surface(shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = messageText,
                                  onValueChange = { messageText = it },
                                  modifier = Modifier.weight(1f),
                                  placeholder = { Text(TYPE_MESSAGE_HINT) },
                                  maxLines = 3,
                                  shape = RoundedCornerShape(24.dp))

                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = SEND_MESSAGE_DESC, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

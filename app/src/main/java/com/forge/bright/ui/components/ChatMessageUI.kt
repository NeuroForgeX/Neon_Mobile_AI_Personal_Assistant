package com.forge.bright.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forge.bright.SEEN
import com.forge.bright.SENDING_MESSAGE
import com.forge.bright.TYPING_INDICATOR
import com.forge.bright.db.o.ChatMessage
import com.forge.bright.db.o.MessageType
import com.forge.bright.utils.DateUtils
import dev.jeziellago.compose.markdowntext.MarkdownText

private const val TAG = "ChatMessageUI.kt"

@Composable
fun ChatMessageItem(message: ChatMessage) {
    when (message.messageType) {
        MessageType.FROM_USER -> FromUserMessageUI(message)
        MessageType.FROM_AI -> FromAIMessageUI(message)
        MessageType.STATIC_NOTIFICATION -> StaticNotificationMessageUI(message)
        MessageType.STATIC_ERROR_NOTIFICATION -> StaticNotificationMessageUI(message)
        MessageType.DYNAMIC_NOTIFICATION -> DynamicNotificationMessageUI(message)
        MessageType.DYNAMIC_SEND_NOTIFICATION -> DynamicSendNotificationMessageUI(message)
        MessageType.DYNAMIC_SEEN_NOTIFICATION -> DynamicSeenNotificationMessageUI(message)
        MessageType.DYNAMIC_TYPING_NOTIFICATION -> DynamicTypingNotificationMessageUI(message)
    }
}

@Composable
fun FromUserMessageUI(message: ChatMessage) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
        Column(modifier = Modifier.fillMaxWidth(0.87f), horizontalAlignment = Alignment.End) {
            Box(modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(12.dp)) {
                MarkdownText(
                    markdown = message.message,
                )
            }
            Text(text = DateUtils.readableTime(message.timestamp), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp, end = 4.dp))
        }
    }
}

@Composable
fun FromAIMessageUI(message: ChatMessage) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Column(modifier = Modifier.fillMaxWidth(0.87f), horizontalAlignment = Alignment.Start) {
            Box(modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(12.dp)) {
                MarkdownText(
                    markdown = message.message,
                )
            }
            Text(text = DateUtils.readableTime(message.timestamp), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp, start = 4.dp))
        }
    }
}

@Composable
fun StaticNotificationMessageUI(message: ChatMessage) {
    Box(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier
                .fillMaxWidth(0.85f)
                .background(color = Color(0xFF90CAF9), // Darker blue
                            shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 5.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(text = message.message, fontSize = 12.sp, color = Color.Red, // Red color text
                     fontWeight = FontWeight.Medium)
                Text(text = DateUtils.readableTime(message.timestamp), fontSize = 10.sp, color = Color.Black, // Black color time
                     modifier = Modifier.padding(top = 0.dp))
            }
        }
    }
}

@Composable
fun DynamicNotificationMessageUI(message: ChatMessage) {
    Box(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp), contentAlignment = Alignment.Center) {
        Surface(modifier = Modifier.padding(horizontal = 10.dp), shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
            Text(text = message.message, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
        }
        Text(text = DateUtils.readableTime(message.timestamp), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
fun DynamicSendNotificationMessageUI(message: ChatMessage) {
    Column(horizontalAlignment = Alignment.End) {
        Box(modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                .padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                Text(text = SENDING_MESSAGE, color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp)
            }
        }
        Text(text = DateUtils.readableTime(message.timestamp), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp, end = 4.dp))
    }
}

@Composable
fun DynamicSeenNotificationMessageUI(message: ChatMessage) {
    Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
        Text(text = SEEN, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 4.dp))
        // Small checkmark icon could be added here
        Text(text = "✓", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun DynamicTypingNotificationMessageUI(message: ChatMessage) {
    Box(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp), contentAlignment = Alignment.CenterStart) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = TYPING_INDICATOR, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSecondaryContainer, fontStyle = FontStyle.Italic)
            // Typing indicator dots
            repeat(3) { index ->
                val delay = index * 100
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(delay.toLong())
                }
                Text(text = "• ", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }
}

// Preview functions for each UI component
@Preview(showBackground = true)
@Composable
fun FromUserMessageUIPreview() {
    MaterialTheme {
        FromUserMessageUI(ChatMessage(topicId = 1,
                                      message = "**Hello!** This is a *user message* with `code` and\n\n- Bullet points\n- **Bold text**\n- *Italic text*",
                                      messageType = MessageType.FROM_USER))
    }
}

@Preview(showBackground = true)
@Composable
fun FromAIMessageUIPreview() {
    MaterialTheme {
        FromAIMessageUI(ChatMessage(topicId = 1,
                                    message = "**Hello!** This is an *AI response* with:\n\n```kotlin\nfun example() {\n    return Hello World\n}\n```\n\nAnd [links](https://example.com) work too!",
                                    messageType = MessageType.FROM_AI))
    }
}

@Preview(showBackground = true)
@Composable
fun StaticNotificationMessageUIPreview() {
    MaterialTheme {
        StaticNotificationMessageUI(ChatMessage(topicId = 1, message = "Chat started", messageType = MessageType.STATIC_NOTIFICATION))
    }
}

@Preview(showBackground = true)
@Composable
fun StaticErrorNotificationMessageUIPreview() {
    MaterialTheme {
        StaticNotificationMessageUI(ChatMessage(topicId = 1, message = "Error: Connection failed", messageType = MessageType.STATIC_ERROR_NOTIFICATION))
    }
}

@Preview(showBackground = true)
@Composable
fun DynamicNotificationMessageUIPreview() {
    MaterialTheme {
        DynamicNotificationMessageUI(ChatMessage(topicId = 1, message = "AI is thinking...", messageType = MessageType.DYNAMIC_NOTIFICATION))
    }
}

@Preview(showBackground = true)
@Composable
fun DynamicSendNotificationMessageUIPreview() {
    MaterialTheme {
        DynamicSendNotificationMessageUI(ChatMessage(topicId = 1, message = "Sending message...", messageType = MessageType.DYNAMIC_SEND_NOTIFICATION))
    }
}

@Preview(showBackground = true)
@Composable
fun DynamicSeenNotificationMessageUIPreview() {
    MaterialTheme {
        DynamicSeenNotificationMessageUI(ChatMessage(topicId = 1, message = "Message seen", messageType = MessageType.DYNAMIC_SEEN_NOTIFICATION))
    }
}

@Preview(showBackground = true)
@Composable
fun DynamicTypingNotificationMessageUIPreview() {
    MaterialTheme {
        DynamicTypingNotificationMessageUI(ChatMessage(topicId = 1, message = "AI is typing...", messageType = MessageType.DYNAMIC_TYPING_NOTIFICATION))
    }
}

@Preview(showBackground = true)
@Composable
fun ChatMessageItemPreview() {
    MaterialTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ChatMessageItem(ChatMessage(topicId = 1, message = "Hello from user!", messageType = MessageType.FROM_USER))
            ChatMessageItem(ChatMessage(topicId = 1, message = "Hello from AI!", messageType = MessageType.FROM_AI))
            ChatMessageItem(ChatMessage(topicId = 1, message = "Chat started", messageType = MessageType.STATIC_NOTIFICATION))
        }
    }
}

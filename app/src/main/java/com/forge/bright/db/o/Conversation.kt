package com.forge.bright.db.o

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

private const val TAG = "Conversation.kt"

// @formatter:off
enum class MessageType {
    FROM_USER,
    FROM_AI,
    STATIC_NOTIFICATION,
    STATIC_ERROR_NOTIFICATION,
    DYNAMIC_NOTIFICATION,
    DYNAMIC_SEND_NOTIFICATION,
    DYNAMIC_SEEN_NOTIFICATION,
    DYNAMIC_TYPING_NOTIFICATION
}

@Entity(tableName = "topics", indices = [Index(value = ["id"], unique = true)])
data class Topic (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val title: String,
    val createdAt: Long = System.currentTimeMillis())

@Entity(tableName = "messages",
        foreignKeys = [ForeignKey(entity = Topic::class,
                                  parentColumns = ["id"],
                                  childColumns = ["topicId"],
                                  onDelete = ForeignKey.CASCADE)],
        indices = [Index(value = ["id"], unique = true), Index(value = ["topicId"])])
data class ChatMessage (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 1,
    val topicId: Int,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: MessageType): Comparable<ChatMessage> {
    override fun compareTo(other: ChatMessage): Int {
        return timestamp.compareTo(other.timestamp)
    }
}
// @formatter:on

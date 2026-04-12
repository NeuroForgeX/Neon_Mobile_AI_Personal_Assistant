package com.forge.bright.db

import android.content.Context
import androidx.room.Database
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import com.forge.bright.db.o.ChatMessage
import com.forge.bright.db.o.Model
import com.forge.bright.db.o.Topic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

private const val TAG = "ConversationDatabase.kt"

@Database(entities = [Topic::class, ChatMessage::class, Model::class], version = 1, exportSchema = false) abstract class ConversationDatabase : RoomDatabase() {

    abstract fun topicDao(): TopicDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun modelDao(): ModelDao

    companion object {
        @Volatile
        private var INSTANCE: ConversationDatabase? = null

        fun getDatabase(context: Context): ConversationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, ConversationDatabase::class.java, "conversation_database").fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class DebugDatabase : ConversationDatabase() {
    override fun topicDao(): TopicDao = MockTopicDao()
    override fun chatMessageDao(): ChatMessageDao = MockChatMessageDao()
    override fun modelDao(): ModelDao = MockModelDao()

    override fun createInvalidationTracker(): InvalidationTracker {
        TODO("Not yet implemented")
    }

    override fun clearAllTables() {
        TODO("Not yet implemented")
    }
}

// Mock implementations for testing
class MockTopicDao : TopicDao {
    override fun getAllTopics(): Flow<List<Topic>> = flowOf(emptyList())
    override suspend fun getTopicById(topicId: Int): Topic? = null
    override suspend fun insertTopic(topic: Topic): Long = 1L
    override suspend fun updateTopic(topic: Topic) {}
    override suspend fun deleteTopic(topic: Topic) {}
    override suspend fun deleteTopicById(topicId: Int) {}
}

class MockChatMessageDao : ChatMessageDao {
    override fun getMessagesForTopic(topicId: Int): Flow<List<ChatMessage>> = flowOf(emptyList())
    override suspend fun getMessageById(messageId: Long): ChatMessage? = null
    override fun getRecentMessages(limit: Int): Flow<List<ChatMessage>> = flowOf(emptyList())
    override suspend fun insertMessage(message: ChatMessage): Long = 1L
    override suspend fun insertMessages(messages: List<ChatMessage>): List<Long> = messages.map { 1L }
    override suspend fun updateMessage(message: ChatMessage) {}
    override suspend fun deleteMessage(message: ChatMessage) {}
    override suspend fun deleteMessagesForTopic(topicId: Int) {}
    override suspend fun deleteMessageById(messageId: Long) {}
    override suspend fun getMessageCountForTopic(topicId: Int): Int = 0
}

class MockModelDao : ModelDao {
    override fun getAllModels(): Flow<List<Model>> = flowOf(emptyList())
    override suspend fun getModelById(modelId: Int): Model? = null
    override suspend fun getModelByName(name: String): Model? = null
    override suspend fun getModelByShortName(shortName: String): Model? = null
    override fun getModelsByOwner(owner: String): Flow<List<Model>> = flowOf(emptyList())
    override suspend fun insertModel(model: Model): Long = 1L
    override suspend fun insertModels(models: List<Model>): List<Long> = models.map { 1L }
    override suspend fun updateModel(model: Model) {}
    override suspend fun deleteModel(model: Model) {}
    override suspend fun deleteModelById(modelId: Int) {}
    override suspend fun getModelCount(): Int = 0
    override suspend fun getModelCountByOwner(owner: String): Int = 0
}

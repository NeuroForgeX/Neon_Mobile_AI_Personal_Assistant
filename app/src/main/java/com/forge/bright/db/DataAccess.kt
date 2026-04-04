package com.forge.bright.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.forge.bright.db.o.ChatMessage
import com.forge.bright.db.o.Model
import com.forge.bright.db.o.Topic
import kotlinx.coroutines.flow.Flow

private const val TAG = "DataAccess.kt"

@Dao interface TopicDao {
    @Query("SELECT * FROM topics ORDER BY createdAt DESC")
    fun getAllTopics(): Flow<List<Topic>>

    @Query("SELECT * FROM topics WHERE id = :topicId")
    suspend fun getTopicById(topicId: Int): Topic?

    @Insert
    suspend fun insertTopic(topic: Topic): Long

    @Update
    suspend fun updateTopic(topic: Topic)

    @Delete
    suspend fun deleteTopic(topic: Topic)

    @Query("DELETE FROM topics WHERE id = :topicId")
    suspend fun deleteTopicById(topicId: Int)
}

@Dao interface ChatMessageDao {
    @Query("SELECT * FROM messages WHERE topicId = :topicId ORDER BY timestamp ASC")
    fun getMessagesForTopic(topicId: Int): Flow<List<ChatMessage>>

    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: Long): ChatMessage?

    @Query("SELECT * FROM messages ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMessages(limit: Int = 50): Flow<List<ChatMessage>>

    @Insert
    suspend fun insertMessage(message: ChatMessage): Long

    @Insert
    suspend fun insertMessages(messages: List<ChatMessage>): List<Long>

    @Update
    suspend fun updateMessage(message: ChatMessage)

    @Delete
    suspend fun deleteMessage(message: ChatMessage)

    @Query("DELETE FROM messages WHERE topicId = :topicId")
    suspend fun deleteMessagesForTopic(topicId: Int)

    @Query("DELETE FROM messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: Long)

    @Query("SELECT COUNT(*) FROM messages WHERE topicId = :topicId")
    suspend fun getMessageCountForTopic(topicId: Int): Int
}

@Dao interface ModelDao {
    @Query("SELECT * FROM models ORDER BY createdAt DESC")
    fun getAllModels(): Flow<List<Model>>

    @Query("SELECT * FROM models WHERE id = :modelId")
    suspend fun getModelById(modelId: Int): Model?

    @Query("SELECT * FROM models WHERE name = :name")
    suspend fun getModelByName(name: String): Model?

    @Query("SELECT * FROM models WHERE shortName = :shortName")
    suspend fun getModelByShortName(shortName: String): Model?

    @Query("SELECT * FROM models WHERE owner = :owner ORDER BY createdAt DESC")
    fun getModelsByOwner(owner: String): Flow<List<Model>>

    @Insert
    suspend fun insertModel(model: Model): Long

    @Insert
    suspend fun insertModels(models: List<Model>): List<Long>

    @Update
    suspend fun updateModel(model: Model)

    @Delete
    suspend fun deleteModel(model: Model)

    @Query("DELETE FROM models WHERE id = :modelId")
    suspend fun deleteModelById(modelId: Int)

    @Query("SELECT COUNT(*) FROM models")
    suspend fun getModelCount(): Int

    @Query("SELECT COUNT(*) FROM models WHERE owner = :owner")
    suspend fun getModelCountByOwner(owner: String): Int
}

object DataAccess {
    private lateinit var database: ConversationDatabase

    fun initialize(context: Context) {
        if (!::database.isInitialized) {
            database = ConversationDatabase.getDatabase(context)
        }
    }

    fun initializeDebug() {
        database = DebugDatabase()
    }

    fun getAllModels(): Flow<List<Model>> {
        return database.modelDao().getAllModels()
    }

    fun getAllTopics(): Flow<List<Topic>> {
        return database.topicDao().getAllTopics()
    }

    fun getMessagesForTopic(topicId: Int): Flow<List<ChatMessage>> {
        return database.chatMessageDao().getMessagesForTopic(topicId)
    }

    suspend fun getModelById(modelId: Int): Model? {
        return database.modelDao().getModelById(modelId)
    }

    suspend fun insertModel(model: Model): Long {
        return database.modelDao().insertModel(model)
    }

    suspend fun updateModel(model: Model) {
        return database.modelDao().updateModel(model)
    }

    suspend fun deleteModel(model: Model) {
        return database.modelDao().deleteModel(model)
    }
}

object debug {
    fun initialize(context: Context) {
        DataAccess.initialize(context)
    }
}
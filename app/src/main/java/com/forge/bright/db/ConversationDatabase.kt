package com.forge.bright.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.forge.bright.db.o.ChatMessage
import com.forge.bright.db.o.Topic
import com.forge.bright.db.o.Model

@Database(
    entities = [Topic::class, ChatMessage::class, Model::class],
    version = 1,
    exportSchema = false
)
abstract class ConversationDatabase : RoomDatabase() {
    
    abstract fun topicDao(): TopicDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun modelDao(): ModelDao
    
    companion object {
        @Volatile
        private var INSTANCE: ConversationDatabase? = null
        
        fun getDatabase(context: Context): ConversationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ConversationDatabase::class.java,
                    "conversation_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

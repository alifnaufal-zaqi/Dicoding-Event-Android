package com.alif.dicodingevent.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alif.dicodingevent.data.local.entity.FavoriteEventEntity

@Database(entities = [FavoriteEventEntity::class], version = 2, exportSchema = true)
abstract class EventDatabase : RoomDatabase() {
    abstract fun favoriteEventDao(): FavoriteEventDao

    companion object {
        @Volatile
        private var instance: EventDatabase? = null

        fun getInstance(context: Context): EventDatabase = instance ?: synchronized(this) {
            instance ?: Room
                .databaseBuilder(context.applicationContext, EventDatabase::class.java, "event_db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
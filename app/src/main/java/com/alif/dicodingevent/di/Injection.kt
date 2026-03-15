package com.alif.dicodingevent.di

import android.content.Context
import com.alif.dicodingevent.data.EventRepository
import com.alif.dicodingevent.data.local.room.EventDatabase
import com.alif.dicodingevent.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getInstance(context)
        val dao = database.favoriteEventDao()

        return EventRepository.getInstance(apiService, dao)
    }
}
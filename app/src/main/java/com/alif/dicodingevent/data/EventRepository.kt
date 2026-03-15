package com.alif.dicodingevent.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.alif.dicodingevent.data.local.entity.FavoriteEventEntity
import com.alif.dicodingevent.data.local.room.FavoriteEventDao
import com.alif.dicodingevent.data.remote.response.ListEventsItem
import com.alif.dicodingevent.data.remote.retrofit.ApiService
import com.alif.dicodingevent.utils.EventType

class EventRepository private constructor(
    private val apiService: ApiService,
    private val favoriteEventDao: FavoriteEventDao,
) {
    fun getFavoriteEvents(): LiveData<Result<List<FavoriteEventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val favoriteEvents: LiveData<Result<List<FavoriteEventEntity>>> = favoriteEventDao.getAllFavoriteEvents().map {
                Result.Success(it)
            }
            emitSource(favoriteEvents)
        } catch (e: Exception) {
            Log.d("EventRepository", "getFavoriteEvents: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun isEventFavorite(id: Int): LiveData<Result<Boolean>> = liveData {
        emit(Result.Loading)
        try {
            val isFavorite: LiveData<Result<Boolean>> = favoriteEventDao.getFavoriteEventById(id).map {
                Result.Success(it != null)
            }
            emitSource(isFavorite)
        } catch (e: Exception) {
            Log.d("EventRepository", "isEventFavorite: ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun getEvents(eventType: EventType): LiveData<Result<List<ListEventsItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getEvents(eventType.active)
            val events = response.listEvents
            emit(Result.Success(events))
        } catch (e: Exception) {
            Log.e("EventRepository", "getEvents: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun getEventById(id: Int): LiveData<Result<ListEventsItem>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getEventById(id)
            val event = response.event
            emit(Result.Success(event))
        } catch (e: Exception) {
            Log.e("EventRepository", "getEventById: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun searchEventByName(name: String): LiveData<Result<List<ListEventsItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.searchEventByName(keyword = name)
            val events = response.listEvents
            emit(Result.Success(events))
        } catch (e: Exception) {
            Log.e("EventRepository", "searchEventByName: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun addFavoriteEvent(event: FavoriteEventEntity) {
        favoriteEventDao.insertFavoriteEvent(event)
    }

    suspend fun removeFavoriteEvent(id: Int) {
        favoriteEventDao.deleteFavoriteEvent(id)
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(
            apiService: ApiService,
            favoriteEventDao: FavoriteEventDao
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(apiService, favoriteEventDao)
            }.also { instance = it }
    }
}
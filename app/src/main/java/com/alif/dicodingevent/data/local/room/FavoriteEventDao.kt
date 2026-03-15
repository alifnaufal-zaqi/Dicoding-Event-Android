package com.alif.dicodingevent.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alif.dicodingevent.data.local.entity.FavoriteEventEntity

@Dao
interface FavoriteEventDao {
    @Query("SELECT * FROM favorite_events")
    fun getAllFavoriteEvents(): LiveData<List<FavoriteEventEntity>>

    @Query("SELECT * FROM favorite_events WHERE id = :id")
    fun getFavoriteEventById(id: Int): LiveData<FavoriteEventEntity?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavoriteEvent(event: FavoriteEventEntity)

    @Query("DELETE FROM favorite_events WHERE id = :id")
    suspend fun deleteFavoriteEvent(id: Int)
}
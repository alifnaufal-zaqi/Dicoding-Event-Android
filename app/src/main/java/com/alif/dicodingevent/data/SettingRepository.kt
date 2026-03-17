package com.alif.dicodingevent.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingRepository private constructor(private val dataStore: DataStore<Preferences>) {

    fun getSetting(key: Preferences.Key<Boolean>): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: false
        }
    }

    suspend fun saveSetting(key: Preferences.Key<Boolean>, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    companion object {
        @Volatile
        var instance: SettingRepository? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingRepository = instance ?: synchronized(this) {
            instance ?: SettingRepository(dataStore)
        }.also { instance = it }
    }
}
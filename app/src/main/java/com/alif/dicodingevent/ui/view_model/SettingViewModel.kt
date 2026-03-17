package com.alif.dicodingevent.ui.view_model

import androidx.lifecycle.ViewModel
import com.alif.dicodingevent.data.SettingRepository
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.alif.dicodingevent.data.local.datastore.SettingPreferences
import kotlinx.coroutines.launch

class SettingViewModel(private val settingRepository: SettingRepository) : ViewModel() {
    val themeSetting = settingRepository.getSetting(SettingPreferences.THEME_KEY).asLiveData()
    val notificationSetting = settingRepository.getSetting(SettingPreferences.NOTIFICATION_KEY).asLiveData()

    fun saveSetting(key: Preferences.Key<Boolean>, value: Boolean) {
        viewModelScope.launch {
            settingRepository.saveSetting(key, value)
        }
    }
}
package com.alif.dicodingevent.ui.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alif.dicodingevent.data.EventRepository
import com.alif.dicodingevent.data.SettingRepository
import com.alif.dicodingevent.di.Injection

class ViewModelFactory private constructor(private val eventRepository: EventRepository, private val settingRepository: SettingRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventViewModel::class.java)) {
            return EventViewModel(eventRepository) as T
        } else if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(settingRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory = instance ?: synchronized(this) {
            instance ?: ViewModelFactory(Injection.provideRepository(context), Injection.provideSettingRepository(context))
        }.also { instance = it }
    }
}
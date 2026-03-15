package com.alif.dicodingevent.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alif.dicodingevent.data.EventRepository
import com.alif.dicodingevent.data.Result
import com.alif.dicodingevent.data.local.entity.FavoriteEventEntity
import com.alif.dicodingevent.data.remote.response.ListEventsItem
import com.alif.dicodingevent.utils.Event
import com.alif.dicodingevent.utils.EventType
import kotlinx.coroutines.launch

class EventViewModel(private val eventRepository: EventRepository) : ViewModel() {

    private val _allEvents = MutableLiveData<List<ListEventsItem>>()
    val allEvents: LiveData<List<ListEventsItem>> = _allEvents

    private val _activeComingEvents = MutableLiveData<List<ListEventsItem>>()
    val activeComingEvents: LiveData<List<ListEventsItem>> = _activeComingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _favoriteEvents = MutableLiveData<List<FavoriteEventEntity>>()
    val favoriteEvents: LiveData<List<FavoriteEventEntity>> = _favoriteEvents

    private val _event = MutableLiveData<ListEventsItem>()
    val event: LiveData<ListEventsItem> = _event

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _responseMessage = MutableLiveData<Event<String>>()
    val responseMessage: LiveData<Event<String>> = _responseMessage

    private fun <T> observeRepository(result: Result<T>?, livedata: MutableLiveData<T>) {
        if (result != null) {
            when (result) {
                is Result.Loading -> _isLoading.value = true
                is Result.Success -> {
                    _isLoading.value = false
                    livedata.value = result.data
                }
                is Result.Error -> {
                    _isLoading.value = false
                    _responseMessage.value = Event(result.error)
                }
            }
        }
    }

    fun getFavoriteEvents() {
        eventRepository.getFavoriteEvents().observeForever { result ->
            observeRepository(result, _favoriteEvents)
        }
    }

    fun addToFavoriteEvent(event: FavoriteEventEntity) {
        viewModelScope.launch {
            eventRepository.addFavoriteEvent(event)
        }
    }

    fun isFavoriteEvent(id: Int) = eventRepository.isEventFavorite(id)

    fun removeEventFromFavorite(id: Int) {
        viewModelScope.launch {
            eventRepository.removeFavoriteEvent(id)
        }
    }

    fun getEvents(typeEvent: EventType) {
        viewModelScope.launch {
            eventRepository.getEvents(typeEvent).observeForever { result ->
                val targetEvent = when (typeEvent) {
                    EventType.ACTIVE -> _activeComingEvents
                    EventType.FINISHED -> _finishedEvents
                }

                observeRepository(result, targetEvent)
            }
        }
    }

    fun getDetailEventById(id: Int) {
        viewModelScope.launch {
            eventRepository.getEventById(id).observeForever { result ->
                observeRepository(result, _event)
            }
        }
    }

    fun searchEventByName(name: String) {
        viewModelScope.launch {
            eventRepository.searchEventByName(name).observeForever { result ->
                observeRepository(result, _allEvents)
            }
        }
    }
}
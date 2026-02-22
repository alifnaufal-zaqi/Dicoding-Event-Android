package com.alif.dicodingevent.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alif.dicodingevent.data.response.DetailEventResponse
import com.alif.dicodingevent.data.response.EventResponse
import com.alif.dicodingevent.data.response.ListEventsItem
import com.alif.dicodingevent.data.retrofit.ApiConfig
import com.alif.dicodingevent.utils.Event
import com.alif.dicodingevent.utils.EventType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventViewModel : ViewModel() {
    private val _allEvents = MutableLiveData<List<ListEventsItem>>()
    val allEvents: LiveData<List<ListEventsItem>> = _allEvents

    private val _activeComingEvents = MutableLiveData<List<ListEventsItem>>()
    val activeComingEvents: LiveData<List<ListEventsItem>> = _activeComingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _event = MutableLiveData<ListEventsItem>()
    val event: LiveData<ListEventsItem> = _event

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _responseMessage = MutableLiveData<Event<String>>()
    val responseMessage: LiveData<Event<String>> = _responseMessage

    private var currentEventId: Int? = null

    fun getEvents(typeEvent: EventType) {
        _isLoading.value = true

        val client = ApiConfig
            .getApiService()
            .getEvents(typeEvent.active)

        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()

                if (responseBody != null) {
                    when(typeEvent.active) {
                        1 -> _activeComingEvents.value = responseBody.listEvents
                        0 -> _finishedEvents.value = responseBody.listEvents
                    }

                    _responseMessage.value = Event("Success ${responseBody.message}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _responseMessage.value = Event("Erorr: ${t.message}")
            }
        })
    }

    fun getDetailEventById(id: Int) {
        if (currentEventId == id && _event.value != null) return

        _isLoading.value = true
        currentEventId = id
        val client = ApiConfig.getApiService().getEventById(id)

        client.enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(
                call: Call<DetailEventResponse>,
                response: Response<DetailEventResponse>
            ) {
                _isLoading.value = false

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null) {
                        _event.value = responseBody.event
                        _responseMessage.value = Event("Success ${responseBody.message}")
                    }
                }
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                _isLoading.value = false
                _responseMessage.value = Event("Error: ${t.message}")
            }
        })
    }

    fun searchEventByName(name: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().searchEventByName(keyword = name)

        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(
                call: Call<EventResponse>,
                response: Response<EventResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()

                if (responseBody != null) {
                    _allEvents.value = responseBody.listEvents
                    _responseMessage.value = Event("Success ${responseBody.message}")
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _responseMessage.value = Event("Error: ${t.message}")
            }
        })
    }
}
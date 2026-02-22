package com.alif.dicodingevent.data.retrofit
import com.alif.dicodingevent.data.response.DetailEventResponse
import com.alif.dicodingevent.data.response.EventResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Get list of events
    @GET("events")
    fun getEvents(@Query("active") active: Int): Call<EventResponse>

    // Get Event by ID
    @GET("events/{id}")
    fun getEventById(@Path("id") id: Int): Call<DetailEventResponse>

    // Search Event By Event Name/Title
    @GET("events")
    fun searchEventByName(@Query("active") active: Int = -1, @Query("q") keyword: String): Call<EventResponse>
}
package com.alif.dicodingevent.data.remote.retrofit
import com.alif.dicodingevent.data.remote.response.DetailEventResponse
import com.alif.dicodingevent.data.remote.response.EventResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    // Get list of events
    @GET("events")
    suspend fun getEvents(@Query("active") active: Int): EventResponse

    // Get Event by ID
    @GET("events/{id}")
    suspend fun getEventById(@Path("id") id: Int): DetailEventResponse

    // Search Event By Event Name/Title
    @GET("events")
    suspend fun searchEventByName(@Query("active") active: Int = -1, @Query("q") keyword: String): EventResponse
}
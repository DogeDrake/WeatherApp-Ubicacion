package com.example.ud6weatherapp.data.remotes

import com.example.ud6weatherapp.data.models.OpenWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("onecall")
    fun getWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String = ApiRest.apiKey,
        @Query("units") unit: String = ApiRest.units
    ): Call<OpenWeatherResponse>
}
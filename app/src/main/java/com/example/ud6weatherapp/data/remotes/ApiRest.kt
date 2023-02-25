package com.example.ud6weatherapp.data.remotes

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiRest {
    lateinit var service: ApiService
    val URL = "https://api.openweathermap.org/data/3.0/"
    val URL_IMAGES = "http://openweathermap.org/img/wn/"
    val apiKey = "203f2143a1415511e7dc599757426d0d"
    val lang = "es"
    val units = "metric"


    fun initService() {
        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(ApiService::class.java)
    }
}

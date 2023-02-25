package com.example.ud6weatherapp.App

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ud6weatherapp.data.models.OpenWeatherResponse
import com.example.ud6weatherapp.data.remotes.ApiRest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel : ViewModel() {

    private val _weatherData = MutableLiveData<OpenWeatherResponse>()
    val weatherData: LiveData<OpenWeatherResponse>
        get() = _weatherData

    fun getWeatherData(latitude: Double, longitude: Double) {
        ApiRest.service.getWeatherData(latitude, longitude).enqueue(object :
            Callback<OpenWeatherResponse> {
            override fun onResponse(
                call: Call<OpenWeatherResponse>,
                response: Response<OpenWeatherResponse>
            ) {
                if (response.isSuccessful) {
                    _weatherData.value = response.body()
                }
            }

            override fun onFailure(call: Call<OpenWeatherResponse>, t: Throwable) {
                // Manejar error
            }
        })
    }
}
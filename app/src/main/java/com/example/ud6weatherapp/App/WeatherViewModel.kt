package com.example.ud6weatherapp.App

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ud6weatherapp.data.models.OpenWeatherResponse
import com.example.ud6weatherapp.data.remotes.ApiRest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class WeatherViewModel : ViewModel() {
    // LiveData para almacenar la información de clima actual
    val currentWeatherLiveData = MutableLiveData<CurrentWeather>()
    var data: ArrayList<OpenWeatherResponse.Daily> = ArrayList()

    // Método para obtener el clima actual
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentWeather(latitud: Double, longitud: Double) {
        val call = ApiRest.service.getWeatherData(latitud, longitud)
        call.enqueue(object : Callback<OpenWeatherResponse> {
            override fun onResponse(
                call: Call<OpenWeatherResponse>,
                response: Response<OpenWeatherResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    data.clear()
                    data.addAll(body.daily)
                    val temperatura = body.current.temp.toInt().toString()
                    val humedad = body.current.humidity.toString()
                    val velocidad = body.current.windSpeed.toString()
                    val sensacion = body.current.feelsLike.toInt().toString()
                    val hora = unixTimeToCurrentTime(body.current.dt.toLong())
                    val imagen = body.current.weather.get(0).icon
                    val lugar = body.timezone

                    // Crear un objeto CurrentWeather con la información del clima actual
                    val currentWeather = CurrentWeather(
                        temperatura = temperatura,
                        humedad = humedad,
                        velocidad = velocidad,
                        sensacion = sensacion,
                        hora = hora,
                        imagen = imagen,
                        lugar = lugar,
                        data = data
                    )

                    // Actualizar el LiveData con el objeto CurrentWeather creado
                    currentWeatherLiveData.postValue(currentWeather)
                } else {
                    //Log.e(TAG, response.errorBody()?.string() ?: "Error")
                }
            }

            override fun onFailure(call: Call<OpenWeatherResponse>, t: Throwable) {
                //Log.e(TAG, t.message.toString())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun unixTimeToCurrentTime(unixTime: Long): String {
        val dateTime = LocalDateTime.ofEpochSecond(unixTime, 0, ZoneOffset.UTC)
        val newDateTime = dateTime.plusHours(1)
        val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM, HH:mm", Locale("es"))
        return newDateTime.format(formatter)
    }

    // Clase interna para almacenar la información de clima actual
    data class CurrentWeather(
        val temperatura: String,
        val humedad: String,
        val velocidad: String,
        val sensacion: String,
        val hora: String,
        val imagen: String,
        val lugar: String,
        val data: ArrayList<OpenWeatherResponse.Daily> = ArrayList()
    )
}
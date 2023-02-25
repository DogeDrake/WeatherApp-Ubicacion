package com.example.ud6weatherapp.App

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ud6weatherapp.R
import com.example.ud6weatherapp.data.models.OpenWeatherResponse
import com.example.ud6weatherapp.data.remotes.ApiRest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private lateinit var RVWeather: RecyclerView
    val TAG = "MainActivity"
    var data: ArrayList<OpenWeatherResponse.Daily> = ArrayList()
    private var adapter: WeatherAdapter? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    val myArray = arrayOfNulls<Double>(2)
    private var latitude: Double? = null
    private var longitude: Double? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.app_name)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        //getLocation()
        val location = getLocation()

        if (location != null) {
            latitude = location.first
            Log.i("Array",latitude.toString())
            longitude = location.second
            Log.i("Array",longitude.toString())
        }

        ApiRest.initService()
        getWeather(latitude!!, longitude!!)

        RVWeather = view.findViewById<RecyclerView>(R.id.RVWeather)
        //Mostrar como cuadricula
        val mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        RVWeather.layoutManager = mLayoutManager

        adapter = WeatherAdapter(data) {}
        RVWeather.adapter = adapter


    }

    private fun getWeather(latitud: Double, longitud: Double) {
        val call = ApiRest.service.getWeatherData(latitud, longitud)
        call.enqueue(object : Callback<OpenWeatherResponse> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<OpenWeatherResponse>,
                response: Response<OpenWeatherResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Log.i(TAG, "Entro!")
                    data.clear()
                    data.addAll(body.daily)
                    adapter?.notifyDataSetChanged()
                    val TextGrados = view?.findViewById<TextView>(R.id.GradosMain)
                    val Hora = view?.findViewById<TextView>(R.id.TimeTexto)
                    val TxtViento = view?.findViewById<TextView>(R.id.TextViento)
                    val TxtHumedad = view?.findViewById<TextView>(R.id.TextHumedad)
                    val TxtSensacion = view?.findViewById<TextView>(R.id.TextSensacion)
                    val TxtLugar = view?.findViewById<TextView>(R.id.LugarTexto)
                    //val TextHumedad = view?.findViewById<TextView>(R.id.HumedadText)
                    var ImagenMain = view?.findViewById<ImageView>(R.id.ActualImage)
                    val temperatura = body.current.temp.toInt().toString()
                    val humedad = body.current.humidity.toString()
                    val velocidad = body.current.windSpeed.toString()
                    val sensacion = body.current.feelsLike.toInt().toString()
                    val hora = unixTimeToCurrentTime(body.current.dt.toLong())
                    val imagen = body.current.weather.get(0).icon
                    val lugar = body.timezone

                    Picasso.get().load("https://openweathermap.org/img/wn/" + imagen + "@2x.png")
                        .into(ImagenMain)
                    Hora?.text = hora
                    TextGrados?.text = temperatura + "º"
                    TxtHumedad?.text = humedad + "%"
                    TxtViento?.text = velocidad + " Km/h"
                    TxtSensacion?.text = sensacion + "º"
                    TxtLugar?.text = lugar
                    // Imprimir aqui el listado con logs
                    // Log.d(TAG, body.toString())
                } else {
                    Log.e(TAG, response.errorBody()?.string() ?: "Error")
                }
            }

            override fun onFailure(call: Call<OpenWeatherResponse>, t: Throwable) {
                Log.e(TAG, t.message.toString())
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


    fun getLocation(): Pair<Double, Double>? {
        val locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                object : LocationListener {
                    override fun onLocationChanged(location: Location) {}

                    override fun onProviderDisabled(provider: String) {}

                    override fun onProviderEnabled(provider: String) {}

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                })

            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            return if (location != null) Pair(location.latitude, location.longitude) else Pair(40.3,-3.7)
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            return null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permisos concedidos, llama al método getLocation nuevamente
            getLocation()
        }
    }


}



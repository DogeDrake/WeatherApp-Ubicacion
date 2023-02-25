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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.motion.widget.Debug.getLocation
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var viewModel: WeatherViewModel


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ApiRest.initService()
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)

        val location = getLocation()

        if (location != null) {
            latitude = location.first
            Log.i("Array", latitude.toString())
            longitude = location.second
            Log.i("Array", longitude.toString())
        }

        viewModel.getCurrentWeather(latitude!!, longitude!!)


        viewModel.currentWeatherLiveData.observe(viewLifecycleOwner) { currentWeather ->
            val TextGrados = view.findViewById<TextView>(R.id.GradosMain)
            val Hora = view.findViewById<TextView>(R.id.TimeTexto)
            val TxtViento = view.findViewById<TextView>(R.id.TextViento)
            val TxtHumedad = view.findViewById<TextView>(R.id.TextHumedad)
            val TxtSensacion = view.findViewById<TextView>(R.id.TextSensacion)
            val TxtLugar = view.findViewById<TextView>(R.id.LugarTexto)
            val ImagenMain = view.findViewById<ImageView>(R.id.ActualImage)

            TextGrados.text = currentWeather.temperatura + "º"
            Hora.text = currentWeather.hora
            TxtViento.text = currentWeather.velocidad + " Km/h"
            TxtHumedad.text = currentWeather.humedad + "%"
            TxtSensacion.text = currentWeather.sensacion + "º"
            TxtLugar.text = currentWeather.lugar
            data = currentWeather.data
            Log.i("DATA", data.toString())

            Picasso.get()
                .load("https://openweathermap.org/img/wn/" + currentWeather.imagen + "@2x.png")
                .into(ImagenMain)

            RVWeather = view.findViewById<RecyclerView>(R.id.RVWeather)
            //Mostrar como cuadricula
            val mLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            RVWeather.layoutManager = mLayoutManager

            adapter = WeatherAdapter(data) {}
            Log.i("DATA2", data.toString())
            RVWeather.adapter = adapter
        }
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
            return if (location != null) Pair(location.latitude, location.longitude) else Pair(
                40.3,
                -3.7
            )
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



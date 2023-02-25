package com.example.ud6weatherapp.App

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.ud6weatherapp.R
import com.example.ud6weatherapp.data.models.OpenWeatherResponse
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class WeatherAdapter(
    private val data: ArrayList<OpenWeatherResponse.Daily>,
    val onCLick: (OpenWeatherResponse.Daily) -> Unit
) :
    RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val genero = itemView.findViewById<TextView>(R.id.WeatherText)
        val humedad = itemView.findViewById<TextView>(R.id.HumedadText)
        val daytime = itemView.findViewById<TextView>(R.id.TimeTopDays)
        val card = itemView.findViewById<CardView>(R.id.card)
        val WeatherImagen = itemView.findViewById<ImageView>(R.id.WeatherImahe)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(item: OpenWeatherResponse.Daily) {
            genero.text = item.temp.day.toInt().toString() + " º"
            humedad.text = item.humidity.toString() + "%"
            daytime.text = unixTimeToCurrentTime(item.dt.toLong()).toString().uppercase()
            val imagen = item.weather.get(0).icon
            // val imagen2 = imagen.icon
            Picasso.get().load("https://openweathermap.org/img/wn/" + imagen + "@2x.png")
                .into(WeatherImagen)

            card.setOnClickListener {
                onCLick(item)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun unixTimeToCurrentTime(unixTime: Long): String {
        val dateTime = LocalDateTime.ofEpochSecond(unixTime, 0, ZoneOffset.UTC)
        val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale("es"))
        return dateTime.format(formatter)
    }














}

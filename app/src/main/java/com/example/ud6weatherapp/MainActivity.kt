package com.example.ud6weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ud6weatherapp.App.WeatherFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.container, WeatherFragment())
            .commit()
    }
}
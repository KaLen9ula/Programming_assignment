package com.example.uakpicomsysio8101

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_graphic))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val coordinate = CoordinateMA(35, 27, 62, Direction.LENGTH)
        val coordinate1 = CoordinateMA(13, 43, -32, Direction.LENGTH)

        println(coordinate.getDegreesSecondsMinutesDirection())
        println(coordinate.getDegreesSecondsMinutesDirectionDouble())
        println(coordinate.averageCoordinate(coordinate1))
        println(coordinate.averageCoordinateTwoParameters(coordinate, coordinate1))
    }
}
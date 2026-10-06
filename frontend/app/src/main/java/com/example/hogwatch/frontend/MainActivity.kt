package com.example.hogwatch.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavController
import com.example.hogwatch.frontend.ui.theme.SrcTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hogwatch.frontend.data.remote.NetworkClient
import com.example.hogwatch.frontend.data.repository.LocationRepository
import com.example.hogwatch.frontend.ui.camerasubmission.CameraScreen
import com.example.hogwatch.frontend.ui.map.MapScreen
import com.example.hogwatch.frontend.data.repository.UserManager
import com.example.hogwatch.frontend.ui.home.HomeScreen
import com.example.hogwatch.frontend.ui.manualsubmission.ManualScreen
import com.google.android.gms.location.LocationServices


class MainActivity : ComponentActivity() {
    //private val locationRepository = LocationRepository(application.applicationContext) //Get current location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserManager.getInstance(applicationContext)
        enableEdgeToEdge()
        setContent {
            SrcTheme {
                val navController = rememberNavController()

                // Tab Switching
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(onNavigateToManualReport = {navController.navigate("ManualScreen")},
                            onNavigateToCameraReport = {navController.navigate("CameraScreen")},
                            onNavigateToMap = {navController.navigate("MapScreen")},
                            onNavigateToInfo = {  },//TODO: add navigation
                            onNavigateToAbout = {  }) //TODO: add navigation
                    }
                    composable("CameraScreen") {
                        CameraScreen()
                    }
                    composable("ManualScreen") {
                        ManualScreen(navController =  navController)
                    }
                    composable("MapScreen") {
                        MapScreen()
                    }
                }
            }
        }
        NetworkClient.initClient()
    }
    //onStart()
    //onResume()
    //onStop()
    //onPause()

    override fun onDestroy() {
        super.onDestroy()
        NetworkClient.closeClient()
    }
}

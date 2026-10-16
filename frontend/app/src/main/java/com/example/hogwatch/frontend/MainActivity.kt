package com.example.hogwatch.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hogwatch.frontend.ui.theme.SrcTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hogwatch.frontend.data.remote.NetworkClient
import com.example.hogwatch.frontend.ui.camerasubmission.CameraScreen
import com.example.hogwatch.frontend.ui.map.MapScreen
import com.example.hogwatch.frontend.data.repository.UserManager
import com.example.hogwatch.frontend.ui.sound.SoundScreen
import com.example.hogwatch.frontend.ui.home.HomeScreen
import com.example.hogwatch.frontend.ui.rescue.RescueScreen
import com.example.hogwatch.frontend.ui.info.InfoScreen
import com.example.hogwatch.frontend.ui.manualsubmission.ManualSubmitScreen
//import com.example.hogwatch.frontend.ui.manualsubmission.ManualScreen


class MainActivity : ComponentActivity() {
   //private val locationRepository = LocationRepository(application.applicationContext) //Get current location

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      UserManager.getInstance(applicationContext)
      //SightingRepository.init(applicationContext)
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
                  HomeScreen(
                     onNavigateToManualReport = { navController.navigate("ManualScreen") },
                     onNavigateToCameraReport = { navController.navigate("CameraScreen") },
                     onNavigateToMap = { navController.navigate("MapScreen") },
                     onNavigateToInfo = { navController.navigate("InfoScreen") },
                     onNavigateToSound = { navController.navigate("SoundScreen") },
                     onNavigateToRescue = { navController.navigate("RescueScreen") },
                     onNavigateToAbout = { }) //TODO: add navigation
               }
               composable("CameraScreen") {
                  CameraScreen(onNavigateBack = { navController.popBackStack() } , onNavigateMap = { navController.navigate("MapScreen") })
               }
               composable("ManualScreen") {
                  ManualSubmitScreen(onNavigateBack = { navController.popBackStack() } , onNavigateMap = { navController.navigate("MapScreen") })
               }
               composable("MapScreen") {
                  MapScreen()
               }
               composable("InfoScreen") {
                  InfoScreen(onNavigateBack = { navController.popBackStack() })
               }
               composable("SoundScreen") {
                  SoundScreen(onNavigateBack = { navController.popBackStack() })
               }
               composable("RescueScreen") {
                  RescueScreen(onNavigateBack = { navController.popBackStack() })
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

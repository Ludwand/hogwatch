
package com.example.hogwatch.frontend.data.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationRepository(context: Context) {
   private val appContext = context.applicationContext

   private val locationClient =
      LocationServices.getFusedLocationProviderClient(appContext)

   fun hasPermission(): Boolean {
      return listOf(
         Manifest.permission.ACCESS_FINE_LOCATION,
         Manifest.permission.ACCESS_COARSE_LOCATION
      ).any {
         ContextCompat.checkSelfPermission(appContext, it) ==
                 PackageManager.PERMISSION_GRANTED
      }
   }

   @SuppressLint("MissingPermission")
   fun getUserLocation(onLocation: (Location) -> Unit) {
      if (!hasPermission()) return

      var receivedFreshLocation = false

      // First try cached location (fast).
      locationClient.lastLocation.addOnSuccessListener { location ->
         if (!receivedFreshLocation) {
            location?.let(onLocation)
         }
      }

      // Then request current location.
      locationClient.getCurrentLocation(
         Priority.PRIORITY_HIGH_ACCURACY,
         CancellationTokenSource().token
      ).addOnSuccessListener { location ->
         location?.let {
            receivedFreshLocation = true
            onLocation(it)
         }
      }
   }
}
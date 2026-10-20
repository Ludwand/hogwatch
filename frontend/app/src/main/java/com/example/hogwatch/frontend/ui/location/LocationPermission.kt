
package com.example.hogwatch.frontend.ui.location

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

data class LocationPermissionState(
   val hasPermission: Boolean,
   val requestPermission: () -> Unit
)

@Composable
fun rememberLocationPermission(
   requestOnStart: Boolean = false,
   onPermissionGranted: () -> Unit = {}
): LocationPermissionState {
   val context = LocalContext.current

   val permissions = arrayOf(
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION
   )

   fun checkPermission(): Boolean =
      permissions.any {
         ContextCompat.checkSelfPermission(context, it) ==
                 PackageManager.PERMISSION_GRANTED
      }

   var hasPermission by remember {
      mutableStateOf(checkPermission())
   }

   val currentOnGranted by rememberUpdatedState(onPermissionGranted)

   val launcher = rememberLauncherForActivityResult(
      ActivityResultContracts.RequestMultiplePermissions()
   ) { result ->
      hasPermission = result.values.any { it }

      if (hasPermission) {
         currentOnGranted()
      }
   }

   LaunchedEffect(requestOnStart) {
      if (requestOnStart && !checkPermission()) {
         launcher.launch(permissions)
      }
   }

   return LocationPermissionState(
      hasPermission = hasPermission,
      requestPermission = {
         hasPermission = checkPermission()

         if (hasPermission) {
            currentOnGranted()
         } else {
            launcher.launch(permissions)
         }
      }
   )
}

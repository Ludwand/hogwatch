package com.example.hogwatch.frontend.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.res.painterResource
import com.example.hogwatch.frontend.R
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.collect

// Wrapper used by Google's clustering system.
data class MapPin(
   val id: Int,
   override val position: LatLng,
   override val title: String? = id.toString(),
   override val snippet: String? = null,
   override val zIndex: Float? = null
) : ClusterItem

@SuppressLint("MissingPermission")
@Composable
fun rememberLocationCameraState(): CameraPositionState {
   val context = LocalContext.current
   val cameraState = rememberCameraPositionState()

   var hasPermission by remember {
      mutableStateOf(
         listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
         ).any {
            ContextCompat.checkSelfPermission(context, it) ==
                    PackageManager.PERMISSION_GRANTED
         }
      )
   }

   val launcher = rememberLauncherForActivityResult(
      ActivityResultContracts.RequestMultiplePermissions()
   ) { permissions ->
      Log.d("GPS", "Permission result: $permissions")
      hasPermission = permissions.values.any { it }
   }

   // Request permission when opening the screen.
   LaunchedEffect(Unit) {
      Log.d("GPS", "Initial permission: $hasPermission")

      if (!hasPermission) {
         Log.d("GPS", "Requesting permission")

         launcher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
         ))
      }
   }

   // Fetch location once permission is granted.
   LaunchedEffect(hasPermission) {
      if (hasPermission) {
         val locationClient = LocationServices.getFusedLocationProviderClient(context)

         // First try cached location (fast).
         locationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
               cameraState.position = CameraPosition.fromLatLngZoom(
                  LatLng(it.latitude, it.longitude),
                  8f
               )
            }
         }

         // Then request current location (potentially slower).
         locationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
         ).addOnSuccessListener { location ->
            location?.let {
               cameraState.position = CameraPosition.fromLatLngZoom(
                  LatLng(it.latitude, it.longitude),
                  8f
               )
            }
         }
      }
   }

   return cameraState
}

@SuppressLint("FlowOperatorInvokedInComposition")
@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(viewModel: MapViewModel = viewModel())
{
   val cameraState = rememberLocationCameraState()

   var mapLoaded by remember { mutableStateOf(false) }

   LaunchedEffect(mapLoaded) {
      if (!mapLoaded) return@LaunchedEffect

      snapshotFlow {
         if (cameraState.isMoving) null else cameraState.position
      }
      .filterNotNull()
      .collect {
         val bounds = cameraState.projection?.visibleRegion?.latLngBounds
            ?: return@collect

         viewModel.loadPins(
            bounds.southwest.latitude,
            bounds.northeast.latitude,
            bounds.southwest.longitude,
            bounds.northeast.longitude
         )
      }
   }

   // Main Google Maps view.
   GoogleMap(
      modifier = Modifier.fillMaxSize(),
      cameraPositionState = cameraState,
      onMapLoaded = { mapLoaded = true })
   {
      // Convert backend pin data to ClusterItems.
      val mapPins = viewModel.pins.value.submissions.map { sub ->
         MapPin(
            id = sub.id,
            position = LatLng(sub.lat, sub.lon)
         )
      }

      // Automatically groups nearby pins based on zoom level.
      Clustering(
         items = mapPins,
         onClusterItemClick = { pin ->
            viewModel.selectPin(pin.id)
            true
         }
      )
   }

   // No selected pin -> no popup.
   if (viewModel.selectedPinId == null)
      return

   // Popup shown after clicking a pin.
   AlertDialog(
      onDismissRequest = {
         viewModel.closePopup()
      },
      confirmButton = {},
      title = {
         Text("Sighting ${viewModel.selectedPinId}")
      },
      text = {
         // Vertically arranged popup content with spacing.
         Column(verticalArrangement = Arrangement.spacedBy(12.dp))
         {
            viewModel.selectedPinImage?.let { bitmap ->
               Image(
                  bitmap = bitmap,
                  contentDescription = "Sighting image",
                  modifier = Modifier
                     .fillMaxWidth()
                     .height(180.dp),
                  contentScale = ContentScale.Crop
               )
            }

            // Temporary test text.
            Text(
               text = "Test information",
               modifier = Modifier.padding(horizontal = 4.dp)
            )
         }
      }
   )
}
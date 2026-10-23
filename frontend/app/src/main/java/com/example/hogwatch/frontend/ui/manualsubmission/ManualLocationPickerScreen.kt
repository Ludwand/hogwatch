package com.example.hogwatch.frontend.ui.manualsubmission

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hogwatch.frontend.ui.location.rememberLocationPermission
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

@SuppressLint("MissingPermission")
@Composable
fun ManualLocationPickerScreen(
   initialLocation: LatLng?,
   onLocationSelected: (LatLng) -> Unit,
   onNavigateBack: () -> Unit,
   viewModel: ManualLocationPickerViewModel = viewModel()
) {
   val uiState by viewModel.uiState.collectAsStateWithLifecycle()

   val cameraState = rememberCameraPositionState {
      position = CameraPosition.fromLatLngZoom(
         initialLocation ?: LatLng(62.0, 15.0),
         if (initialLocation != null) 15f else 5f
      )
   }

   val permission = rememberLocationPermission(
      requestOnStart = true
   )

   // Restore an already selected location when reopening the map.
   LaunchedEffect(initialLocation) {
      viewModel.setInitialLocation(initialLocation)
   }

   // Fetch GPS once Android has granted location permission.
   LaunchedEffect(permission.hasPermission) {
      if (permission.hasPermission) {
         viewModel.fetchUserLocation()
      }
   }

   // Center the camera on the user's GPS position.
   LaunchedEffect(uiState.userLocation) {
      uiState.userLocation?.let { location ->
         cameraState.position =
            CameraPosition.fromLatLngZoom(
               location,
               15f
            )
      }
   }

   Box(
      modifier = Modifier.fillMaxSize()
   ) {
      GoogleMap(
         modifier = Modifier.fillMaxSize(),
         cameraPositionState = cameraState,
         onMapClick = viewModel::selectLocation
      ) {
         uiState.selectedLocation?.let { position ->
            Marker(
               state = rememberUpdatedMarkerState(position)
            )
         }
      }

      Button(
         onClick = onNavigateBack,
         modifier = Modifier
            .align(Alignment.TopStart)
            .statusBarsPadding()
            .padding(16.dp)
      ) {
         Text("Back")
      }

      Button(
         onClick = {
            uiState.selectedLocation?.let {
               onLocationSelected(it)
            }
         },
         enabled = uiState.selectedLocation != null,
         modifier = Modifier
            .align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .padding(24.dp)
      ) {
         Text("Confirm location")
      }
   }
}
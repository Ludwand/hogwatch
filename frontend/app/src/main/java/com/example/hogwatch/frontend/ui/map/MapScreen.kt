package com.example.hogwatch.frontend.ui.map

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hogwatch.frontend.data.repository.LocationRepository
import com.example.hogwatch.frontend.ui.location.rememberLocationPermission
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.filterNotNull

// Wrapper used by Google's clustering system.
data class MapPin(
   val id: Int,
   override val position: LatLng,
   override val title: String? = id.toString(),
   override val snippet: String? = null,
   override val zIndex: Float? = null
) : ClusterItem

@Composable
fun rememberLocationCameraState(): CameraPositionState {
   val context = LocalContext.current
   val cameraState = rememberCameraPositionState()

   val locationRepo = remember(context) {
      LocationRepository(context)
   }

   val permission = rememberLocationPermission(requestOnStart = true)

   LaunchedEffect(permission.hasPermission) {
      if (permission.hasPermission) {
         locationRepo.getUserLocation { location ->
            cameraState.position = CameraPosition.fromLatLngZoom(
               LatLng(location.latitude, location.longitude),
               8f
            )
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
      val mapPins = viewModel.mapState.pins.submissions.map { sub ->
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
   if (viewModel.mapState.selectedPinId == null)
      return

   // Popup shown after clicking a pin.
   AlertDialog(
      onDismissRequest = {
         viewModel.closePopup()
      },
      confirmButton = {},
      title = {
         Text("Sighting ${viewModel.mapState.selectedPinId}")
      },
      text = {
         // Vertically arranged popup content with spacing.
         Column(verticalArrangement = Arrangement.spacedBy(12.dp))
         {
            viewModel.mapState.selectedPinImage?.let { bitmap ->
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
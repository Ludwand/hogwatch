package com.example.hogwatch.frontend.ui.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.hogwatch.frontend.data.remote.NetworkClient
import com.example.hogwatch.shared.Sightings
import android.util.Base64
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MapState(
   val pins: Sightings = Sightings(),
   val selectedPinImage: ImageBitmap? = null,
   val selectedPinId: Int? = null
)

class MapViewModel : ViewModel() {
   var mapState by mutableStateOf(MapState())
      private set

   suspend fun loadPins(
      minLat: Double,
      maxLat: Double,
      minLon: Double,
      maxLon: Double
   ) {
      val pins = NetworkClient.getMapPins(minLat, maxLat, minLon, maxLon)

      mapState = mapState.copy(pins = pins)
   }

   fun selectPin(id: Int) {
      mapState = mapState.copy(
         selectedPinId = id,
         selectedPinImage = null
      )

      viewModelScope.launch {
         val pinInfo = NetworkClient.getMapPinInfo(id)

         val bitmap = withContext(Dispatchers.Default) {
            pinInfo?.image?.let { encoded ->
               val bytes = Base64.decode(encoded, Base64.DEFAULT)
               BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            }
         }

         if (mapState.selectedPinId == id) {
            mapState = mapState.copy(selectedPinImage = bitmap)
         }
      }
   }

   fun closePopup() {
      mapState = mapState.copy(
         selectedPinId = null,
         selectedPinImage = null
      )
   }
}
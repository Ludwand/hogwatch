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

class MapViewModel : ViewModel() {
    val pins = mutableStateOf(Sightings())

   var selectedPinImage by mutableStateOf<ImageBitmap?>(null)
      private set

    var selectedPinId by mutableStateOf<Int?>(null)
        private set
   suspend fun loadPins(
      minLat: Double,
      maxLat: Double,
      minLon: Double,
      maxLon: Double)
   {
      pins.value =
         NetworkClient.getMapPins(minLat, maxLat, minLon, maxLon)

      println("Pins" + pins.value)
   }

   fun selectPin(id: Int) {
      selectedPinId = id
      selectedPinImage = null

      viewModelScope.launch {
         val pinInfo = NetworkClient.getMapPinInfo(id)

         val bitmap = withContext(Dispatchers.Default) {
            pinInfo?.image?.let { encoded ->
               val bytes = Base64.decode(encoded, Base64.DEFAULT)
               BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
            }
         }

         if (selectedPinId == id) {
            selectedPinImage = bitmap
         }
      }
   }

    fun closePopup() {
        selectedPinId = null
    }
}
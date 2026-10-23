package com.example.hogwatch.frontend.ui.manualsubmission

import android.Manifest
import android.app.Application
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import com.example.hogwatch.frontend.data.repository.LocationRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ManualLocationPickerState(
   val userLocation: LatLng? = null,
   val selectedLocation: LatLng? = null
)

class ManualLocationPickerViewModel(
   application: Application
) : AndroidViewModel(application) {

   private val locationRepo = LocationRepository(application)

   private val _uiState =
      MutableStateFlow(ManualLocationPickerState())

   val uiState: StateFlow<ManualLocationPickerState> =
      _uiState.asStateFlow()

   @RequiresPermission(
      allOf = [
         Manifest.permission.ACCESS_FINE_LOCATION,
         Manifest.permission.ACCESS_COARSE_LOCATION
      ]
   )
   fun fetchUserLocation() {
      locationRepo.getUserLocation { location ->
         _uiState.update {
            it.copy(
               userLocation = LatLng(
                  location.latitude,
                  location.longitude
               )
            )
         }
      }
   }

   fun selectLocation(position: LatLng) {
      _uiState.update {
         it.copy(selectedLocation = position)
      }
   }

   fun setInitialLocation(position: LatLng?) {
      _uiState.update {
         it.copy(selectedLocation = position)
      }
   }
}
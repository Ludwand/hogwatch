package com.example.hogwatch.frontend.ui.manualsubmission


import android.Manifest
import android.app.Application
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hogwatch.frontend.data.remote.NetworkClient
import com.example.hogwatch.frontend.data.repository.LocationRepository
import com.example.hogwatch.frontend.data.repository.SightingRepository
import com.example.hogwatch.frontend.data.repository.UserManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

//https://developer.android.com/topic/architecture/ui-layer/stateholders
data class ManualReportUiState(
   val latitudeString: String = "",
   val longitudeString: String = "",
   val date: String = "",
   val time: String = "",
   val isSubmitting: Boolean = false,
   val isSubmittedSuccess: Boolean = false,
   val errorMessage: String? = null,
   var showDatePicker: Boolean = false,
   var showTimePicker: Boolean = false
)

class ManualViewModel(application: Application) : AndroidViewModel(application) {
   private val userManager = UserManager.getInstance(application)

   //private val locationRepo = LocationRepository.getInstance(application) //For fetching location

   //https://www.baeldung.com/kotlin/current-date-time
   private val initialDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
   private val initialTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

   //https://developer.android.com/kotlin/flow/stateflow-and-sharedflow
   private val _uiState =
      MutableStateFlow(ManualReportUiState(date = initialDate, time = initialTime))
   val uiState: StateFlow<ManualReportUiState> = _uiState.asStateFlow()

   fun onSubmit() {
      val state = _uiState.value
      val lat = state.latitudeString.toDoubleOrNull()
      val lon = state.longitudeString.toDoubleOrNull()

      if (lat == null || lon == null) {
         _uiState.update { it.copy(errorMessage = "Latitude or longitude isn't correct formated") }
         return
      }//TODO: check if the coordinate is in Sweden

      val epochSeconds = convertToEpochSeconds(state.date, state.time)

      if (epochSeconds == null) {
         _uiState.update { it.copy(errorMessage = "Wrong format. Use yyyy-mm-dd, hh:mm") }
         return
      }

      viewModelScope.launch {
         _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
         try {
            val userId = userManager.getUserId()

            SightingRepository.submit(userId, lat, lon, epochSeconds, null)

            _uiState.update { it.copy(isSubmitting = false, isSubmittedSuccess = true) }

         } catch (e: Exception) {
            _uiState.update {
               it.copy(
                  isSubmitting = false,
                  errorMessage = e.message ?: "Something went wrong with submitting"
               )
            }
         }
      }

   }

   @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
   fun fetchUserLocation() {
      _uiState.update { it.copy(longitudeString = "11.9748") }
      _uiState.update { it.copy(latitudeString = "57.6892") }
      println("Fetching user location - not implemented yet")
      //TODO: fetch user location
   }

   fun onDateChange(newDate: String) {
      _uiState.update { it.copy(date = newDate) }
   }

   fun onTimeChange(newTime: String) {
      _uiState.update { it.copy(time = newTime) }
   }


   fun onLongitudeChange(lon: String) {
      _uiState.update { it.copy(longitudeString = lon) }
   }

   fun onLatitudeChange(lat: String) {
      _uiState.update { it.copy(latitudeString = lat) }
   }

   //https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html#atTime-int-int-
   fun convertToEpochSeconds(dateStr: String, timeStr: String): Long? {
      return try {
         val localDateTime = LocalDate.parse(dateStr).atTime(LocalTime.parse(timeStr))

         localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
      } catch (e: Exception) {
         null
         //If the formating is wrong
      }
   }


}
package com.example.hogwatch.frontend.ui.manualsubmission


import android.Manifest
import android.app.Application
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.hogwatch.frontend.data.repository.LocationRepository
import com.example.hogwatch.frontend.data.repository.SightingRepository
import com.example.hogwatch.frontend.data.repository.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import android.util.Base64

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
   var showTimePicker: Boolean = false,
   var isAlive: String = "Alive",
   val radioOptions: List<String> = listOf("Alive", "Injured", "Deceased"),
   var photo: Uri? = null
)

class ManualViewModel(application: Application) : AndroidViewModel(application) {
   private val userManager = UserManager.getInstance(application)

   private val locationRepo = LocationRepository(application)

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
      } else if (!inSweden(lat, lon)) { // https://sv.wikipedia.org/wiki/Sveriges_ytterpunkter
         _uiState.update { it.copy(errorMessage = "The sighting must be in Sweden") }
         return
      }

      val epochSeconds = convertToEpochSeconds(state.date, state.time)

      if (epochSeconds == null) {
         _uiState.update { it.copy(errorMessage = "Wrong format. Use yyyy-mm-dd, hh:mm") }
         return
      }

      viewModelScope.launch {
         _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
         try {
            val userId = userManager.getUserId()
            var photoString: String? = null
            if (state.photo != null) {
               photoString = getBase64ForUriAndPossiblyCrash(state.photo!!)
            }

            if (SightingRepository.submit(
                  userId, lat, lon, epochSeconds,
                  photoString, state.isAlive
               )
            ) {
               _uiState.update { it.copy(isSubmitting = false, isSubmittedSuccess = true) }
            } else {
               _uiState.update {
                  it.copy(
                     isSubmitting = false,
                     isSubmittedSuccess = false,
                     errorMessage = "Submission failed due to a network error."
                  )
               }
            }


         } catch (e: Exception) {
            _uiState.update {
               it.copy(
                  isSubmitting = false,
                  errorMessage = e.message ?: "Something went wrong when submitting"
               )
            }
         }
      }

   }

   @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
   fun fetchUserLocation() {
      locationRepo.getUserLocation { location ->
         _uiState.update {
            it.copy(
               latitudeString = location.latitude.toString(),
               longitudeString = location.longitude.toString()
            )
         }
      }
   }

   fun onDateChange(newDate: String) {
      _uiState.update { it.copy(date = newDate) }
   }

   fun onTimeChange(newTime: String) {
      val formatter = DateTimeFormatter.ofPattern("HH:mm")
      val parsedNewTime = LocalTime.parse(newTime, formatter)
      val now = LocalTime.now()

      if ((uiState.value.date.compareTo(initialDate) == 0) && (parsedNewTime.isAfter(now))) {
         _uiState.update { it.copy(errorMessage = "You can't report a hedgehog in the future.") }
         _uiState.update { it.copy(time = now.format(DateTimeFormatter.ofPattern("HH:mm"))) }
      } else {
         _uiState.update { it.copy(time = newTime) }
      }

   }


   fun onLongitudeChange(lon: String) {
      _uiState.update { it.copy(longitudeString = lon) }
   }

   fun onLatitudeChange(lat: String) {
      _uiState.update { it.copy(latitudeString = lat) }
   }

   fun onRadioButton(option: String) {
      _uiState.update { it.copy(isAlive = option) }
   }

   fun onAddPhoto(photo: Uri) {
      _uiState.update { it.copy(photo = photo) }
   }

   fun onRemovePhoto() {
      _uiState.update { it.copy(photo = null) }
   }

   //https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html#atTime-int-int-
   fun convertToEpochSeconds(dateStr: String, timeStr: String): Long? {
      return try {
         val localDateTime = LocalDate.parse(dateStr).atTime(LocalTime.parse(timeStr))

         localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()
      } catch (e: Exception) {
         null
      }
   }


   fun inSweden(lat: Double, lon: Double): Boolean {
      val maxLat = 69.0336
      val minLat = 55.2013
      val maxLon = 24.100
      val minLon = 10.5727

      return lat in minLat..maxLat && lon in minLon..maxLon
   }

   // Source - https://stackoverflow.com/a/57770481
// Posted by CommonsWare
// Retrieved 2026-10-15, License - CC BY-SA 4.0
   //Modified to fit our application

   private fun getBase64ForUriAndPossiblyCrash(uri: Uri): String? {
      try {
         val bytes = application.contentResolver.openInputStream(uri)?.readBytes()
         return Base64.encodeToString(bytes, Base64.DEFAULT)!!
      } catch (error: IOException) {
         error.printStackTrace() // This exception always occurs
         return null
      }
   }
}



package com.example.hogwatch.frontend.ui.camerasubmission

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hogwatch.frontend.data.repository.SightingRepository
import com.example.hogwatch.frontend.data.repository.UserManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class CameraUiState(
   val date: String = "",
   val time: String = "",
   val userId: String? = null,
   val capturedImageBase64: String? = null, //preview image
   val isSubmitting: Boolean = false,
   val errorMessage: String? = null,
   val isSubmittedSuccess: Boolean = false
)

class CameraViewModel(application: Application) : AndroidViewModel(application) {
   private val userManager = UserManager.getInstance(application)

   private val _uiState = MutableStateFlow(CameraUiState())
   val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

   init {
      viewModelScope.launch {
         val userId = userManager.getUserId()
         _uiState.update { it.copy(userId = userId) }
      }
   }

   fun onSubmit() {
      val state = _uiState.value

      if (state.isSubmitting) return

      val userId = state.userId
      val image = state.capturedImageBase64

      if (userId.isNullOrEmpty()) {
         _uiState.update { it.copy(errorMessage = "User ID missing.") }
         return
      }

      if (image.isNullOrEmpty()) {
         _uiState.update { it.copy(errorMessage = "No photo captured.") }
         return
      }

      viewModelScope.launch {
         _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

         try {
            val success = SightingRepository.submit(
               userId = userId,
               lat = 57.6282764,
               lon = 11.9030166,
               timeStamp = System.currentTimeMillis() / 1000L,
               image = image,
               isAlive = "Alive"
            )

            if (success) {
               println("Successfully submitted photo")
               _uiState.update {
                  it.copy(
                     capturedImageBase64 = null,
                     isSubmittedSuccess = true
                  )
               }
            } else {
               println("Failed to submit photo")
               _uiState.update {
                  it.copy(errorMessage = "Submission failed or rate-limited. Wait a moment and try again.")
               }
            }
         } catch (e: Exception) {
            _uiState.update {
               it.copy(errorMessage = e.message ?: "An unexpected error occurred.")
            }
         } finally {
            _uiState.update { it.copy(isSubmitting = false) }
         }
      }
   }


   fun clearSuccessMessage() {
      _uiState.update { it.copy(isSubmittedSuccess = false) }
   }
   fun onPhotoCaptured(base64Image: String) {
      _uiState.update {
         it.copy(
            capturedImageBase64 = base64Image,
            isSubmitting = false
         )
      }
   }

   fun clearErrorMessage() {
      _uiState.update { it.copy(errorMessage = null) }
   }

   fun onRetakePhoto() {
      _uiState.update {
         it.copy(
            capturedImageBase64 = null,
            isSubmitting = false
         )
      }
   }
}
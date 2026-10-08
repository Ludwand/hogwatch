package com.example.hogwatch.frontend.ui.camerasubmission

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.hogwatch.frontend.data.repository.UserManager
import com.example.hogwatch.frontend.ui.manualsubmission.ManualReportUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CameraUiState(
   val date: String = "",
   val time: String = "",
   val userId: String = ""
   //TODO:ersätt med riktiga states för kamera
)

class CameraViewModel(application: Application) : AndroidViewModel(application) {
   private val userManager = UserManager.getInstance(application)

   private val _uiState = MutableStateFlow(CameraUiState())
   val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

   suspend fun onSubmit() {
      val state = _uiState.value
      val userId = userManager.getUserId()
   }
}
package com.example.hogwatch.frontend.ui.viewusersubmissions

import android.app.Application
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hogwatch.frontend.data.repository.SightingRepository
import com.example.hogwatch.frontend.data.repository.UserManager
import com.example.hogwatch.shared.SightingSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UserSubmissionsUiState(
    val userId: String? = null,
    val submissions: List<SightingSummary> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val selectedSighting: SightingSummary? = null,
    val selectedSightingImage: ImageBitmap? = null,
    val isImageLoading: Boolean = false
)

class ViewUserSubmissionsModel(application: Application) : AndroidViewModel(application) {
    private val userManager = UserManager.getInstance(application)

    private val _uiState = MutableStateFlow(UserSubmissionsUiState())
    val uiState: StateFlow<UserSubmissionsUiState> = _uiState.asStateFlow()

    init {
        loadSubmissions()
    }

    fun loadSubmissions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val userId = userManager.getUserId()

            if (userId.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "User ID not found."
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(userId = userId) }

            try {
                val list = SightingRepository.getUserSightings(userId)
                _uiState.update {
                    it.copy(
                        submissions = list,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to fetch submissions."
                    )
                }
            }
        }
    }

    fun selectSighting(sighting: SightingSummary) {
        _uiState.update {
            it.copy(
                selectedSighting = sighting,
                selectedSightingImage = null,
                isImageLoading = sighting.hasImage
            )
        }

        if (sighting.hasImage) {
            viewModelScope.launch {
                val base64Image = SightingRepository.getSightingImage(sighting.id)

                val bitmap = withContext(Dispatchers.Default) {
                    base64Image?.let { encoded ->
                        val bytes = Base64.decode(encoded, Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                    }
                }

                if (_uiState.value.selectedSighting?.id == sighting.id) {
                    _uiState.update {
                        it.copy(
                            selectedSightingImage = bitmap,
                            isImageLoading = false
                        )
                    }
                }
            }
        }
    }

    fun closeDetailPopup() {
        _uiState.update {
            it.copy(
                selectedSighting = null,
                selectedSightingImage = null,
                isImageLoading = false
            )
        }
    }

    fun deleteSubmission(sightingId: Int) {
        val userId = _uiState.value.userId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val success = SightingRepository.deleteSighting(sightingId, userId)
                if (success) {
                    _uiState.update { state ->
                        state.copy(
                            submissions = state.submissions.filterNot { it.id == sightingId }, //local refresh to not pull from server again
                            isLoading = false,
                            successMessage = "Submission deleted successfully."
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to delete submission."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error deleting submission."
                    )
                }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
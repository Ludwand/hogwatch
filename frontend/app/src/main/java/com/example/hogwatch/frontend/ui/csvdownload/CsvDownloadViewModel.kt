package com.example.hogwatch.frontend.ui.csvdownload

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hogwatch.frontend.data.remote.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.OutputStream

data class CsvDownloadUiState(
    val isDownloading: Boolean = false,
    val errorMessage: String? = null,
    val isDownloadingSuccess: Boolean = false
)

class CsvDownloadViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState =
        MutableStateFlow(CsvDownloadUiState())
    val uiState: StateFlow<CsvDownloadUiState> = _uiState.asStateFlow()

    fun onDownload(function: () -> OutputStream?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDownloading = true, errorMessage = null) }
            try {
                val byteArray = NetworkClient.getCsv()

                if (byteArray != null) {
                    writeOutputStream(byteArray, function)
                    _uiState.update { it.copy(isDownloading = false, isDownloadingSuccess = true) }
                } else {
                    _uiState.update {
                        it.copy(
                            isDownloading = false,
                            isDownloadingSuccess = false,
                            errorMessage = "Download failed due to a network error."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        errorMessage = e.message ?: "Something went wrong when downloading"
                    )
                }
            }
        }

    }

    fun writeOutputStream(byteArray: ByteArray, stream: () -> OutputStream?) {
        val outputStream = stream()
            ?: throw IllegalStateException("The file could not be downloaded")

        outputStream.use { out ->
            out.write(byteArray)
        }
    }
}
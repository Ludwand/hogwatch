package com.example.hogwatch.frontend.ui.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.hogwatch.frontend.data.remote.NetworkClient
import com.example.hogwatch.shared.Sightings

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    val pins = mutableStateOf(Sightings())

    var selectedPinId by mutableStateOf<Int?>(null)
        private set

    init {
        viewModelScope.launch {
            pins.value = NetworkClient.getMapInfo()
        }
    }

    fun selectPin(id: Int) {
        selectedPinId = id
    }

    fun closePopup() {
        selectedPinId = null
    }
}
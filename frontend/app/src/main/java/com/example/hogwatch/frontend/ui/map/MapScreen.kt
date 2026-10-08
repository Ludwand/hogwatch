package com.example.hogwatch.frontend.ui.map
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.hogwatch.frontend.data.remote.NetworkClient
import com.example.hogwatch.shared.Sightings
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberUpdatedMarkerState
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MapScreen()
{
    val data = remember { mutableStateOf(Sightings()) }

    LaunchedEffect(Unit) {
        data.value = NetworkClient.getMapInfo()
        println(data)
    }

    GoogleMap(modifier = Modifier.fillMaxSize()) {
        for (sub in data.value.submissions) {
            Marker(
                title = sub.id,
                state = rememberUpdatedMarkerState(LatLng(sub.lat, sub.lon))
            )
        }
    }
}
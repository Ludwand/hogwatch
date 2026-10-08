package com.example.hogwatch.frontend.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.res.painterResource
import com.example.hogwatch.frontend.R
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.compose.MapsComposeExperimentalApi

// Wrapper used by Google's clustering system.
data class MapPin(
   val id: Int,
   override val position: LatLng,
   override val title: String? = id.toString(),
   override val snippet: String? = null,
   override val zIndex: Float? = null
) : ClusterItem

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun MapScreen(viewModel: MapViewModel = viewModel())
{
   // Main Google Maps view.
   GoogleMap(modifier = Modifier.fillMaxSize()) {
      // Convert backend pin data to ClusterItems.
      val mapPins = viewModel.pins.value.submissions.map { sub ->
         MapPin(
            id = sub.id,
            position = LatLng(sub.lat, sub.lon)
         )
      }

      // Automatically groups nearby pins based on zoom level.
      Clustering(
         items = mapPins,
         onClusterItemClick = { pin ->
            viewModel.selectPin(pin.id)
            true
         }
      )
   }

   // No selected pin -> no popup.
   if (viewModel.selectedPinId == null)
      return

   // Popup shown after clicking a pin.
   AlertDialog(
      onDismissRequest = {
         viewModel.closePopup()
      },
      confirmButton = {},
      title = {
         Text("Sighting ${viewModel.selectedPinId}")
      },
      text = {
         // Vertically arranged popup content with spacing.
         Column(verticalArrangement = Arrangement.spacedBy(12.dp))
         {
            // Temporary test image.
            Image(
               painter = painterResource(R.drawable.hedgehog_pic),
               contentDescription = "Test image",
               modifier = Modifier
                  .fillMaxWidth()
                  .height(180.dp)
                  .clip(RoundedCornerShape(12.dp)),
               contentScale = ContentScale.Crop
            )

            // Temporary test text.
            Text(
               text = "Test information",
               modifier = Modifier.padding(horizontal = 4.dp)
            )
         }
      }
   )
}
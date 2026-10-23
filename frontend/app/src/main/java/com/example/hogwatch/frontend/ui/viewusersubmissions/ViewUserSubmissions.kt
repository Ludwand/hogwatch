package com.example.hogwatch.frontend.ui.viewusersubmissions

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hogwatch.shared.SightingSummary
import androidx.compose.foundation.Image

@Composable
fun ViewUserSubmissionsScreen(
    viewModel: ViewUserSubmissionsModel = viewModel(),
    onSightingClick: (Int) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var itemToDelete by remember { mutableStateOf<SightingSummary?>(null) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearSuccessMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        if (uiState.isLoading && uiState.submissions.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.submissions.isEmpty()) {
            Text(
                text = "No submissions found.",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.submissions,
                    key = { it.id }
                ) { sighting ->
                    SightingCard(
                        sighting = sighting,
                        onClick = {
                            viewModel.selectSighting(sighting)
                            onSightingClick(sighting.id)
                        },
                        onDeleteClick = { itemToDelete = sighting }
                    )
                }
            }
        }

        // pop-up on click
        uiState.selectedSighting?.let { sighting ->
            AlertDialog(
                onDismissRequest = { viewModel.closeDetailPopup() },
                title = { Text("Sighting #${sighting.id}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (uiState.isImageLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            uiState.selectedSightingImage?.let { bitmap ->
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = "Sighting image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Text(
                            text = "Latitude: ${sighting.lat}\nLongitude: ${sighting.lon}", //Probably time & date here instead of coords which is useless to end user
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.closeDetailPopup() }) {
                        Text("Close")
                    }
                }
            )
        }

        // Confirm delete
        itemToDelete?.let { sighting ->
            AlertDialog(
                onDismissRequest = { itemToDelete = null },
                title = { Text("Delete Submission") },
                text = { Text("Are you sure you want to delete submission #${sighting.id}?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteSubmission(sighting.id)
                            itemToDelete = null
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SightingCard(
    sighting: SightingSummary,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Sighting #${sighting.id}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Location: ${"%.4f".format(sighting.lat)}, ${"%.4f".format(sighting.lon)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (sighting.hasImage) {
                    Text(
                        text = "Has photo preview",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Submission",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
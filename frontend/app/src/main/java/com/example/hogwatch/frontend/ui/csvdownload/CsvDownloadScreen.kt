package com.example.hogwatch.frontend.ui.csvdownload

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CsvScreen(
    viewModel: CsvDownloadViewModel = viewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    //https://developer.android.com/training/data-storage/shared/documents-files
    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/comma-separated-values")
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.onDownload() {
                context.contentResolver.openOutputStream(uri)
            }
        }
    }

    LaunchedEffect(uiState.isDownloadingSuccess) {
        if (uiState.isDownloadingSuccess) {
            Toast.makeText(context, "The file has been saved!", Toast.LENGTH_SHORT).show()
        }
    }

    CsvContent(
        uiState = uiState,
        onDownloadClick = {
            createDocumentLauncher.launch("sightings.csv")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CsvContent(
    modifier: Modifier = Modifier,
    uiState: CsvDownloadUiState,
    onDownloadClick: () -> Unit,
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Download Csv Data") }
            )
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = onDownloadClick,
                enabled = !uiState.isDownloading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isDownloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                    )
                } else {
                    Text("Download Data")
                }
            }
        }
    }

}
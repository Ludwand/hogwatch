package com.example.hogwatch.frontend.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.hogwatch.frontend.R
import com.example.hogwatch.frontend.ui.navigation.HomeMenu


//https://www.imageonlinetools.com/frutiger-aero-icon-generator
@Composable
fun HomeScreen(
    onNavigateToManualReport: () -> Unit,
    onNavigateToCameraReport: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToInfo: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    HomeScreenContent(
        onManualReportClick = onNavigateToManualReport,
        onCameraReportClick = onNavigateToCameraReport,
        onMapClick = onNavigateToMap,
        onInfoClick = onNavigateToInfo,
        onAboutClick = onNavigateToAbout
    )
}

@Composable
fun HomeScreenContent(
    onManualReportClick: ()-> Unit,
    onCameraReportClick: ()-> Unit,
    onInfoClick: ()-> Unit,
    onMapClick: ()-> Unit,
    onAboutClick: ()-> Unit
){
    StartupDialog()

    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(modifier = Modifier.statusBarsPadding()) {
                HomeMenu(
                    onInfoClick = onInfoClick,
                    onAboutClick = onAboutClick,
                    onMapClick = onMapClick
                )
            }

        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("HogWatch")

            Spacer(modifier = Modifier.height(200.dp))

            CameraButton(onCameraReportClick = onCameraReportClick)
            Spacer(modifier = Modifier.height((24.dp)))
            ManualButton ( onManualReportClick = onManualReportClick )
        }
    }

}

@Composable
fun CameraButton(
    onCameraReportClick: () -> Unit
) {
    IconButton(
        onClick = onCameraReportClick,
        modifier = Modifier.size(150.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.camera),
            contentDescription = "Camera"
        )
    }
}


@Composable
fun ManualButton(
    onManualReportClick: () -> Unit
) {
    IconButton(
        onClick = onManualReportClick,
        modifier = Modifier.size(150.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.plus),
            contentDescription = "Manual Submission"
        )
    }
}
@Composable
fun StartupDialog() {
    // Starts as 'true' every time the app is launched
    var showStartupDialog by rememberSaveable { mutableStateOf(true) }

    if (showStartupDialog) {
        AlertDialog(
            onDismissRequest = {
                showStartupDialog = false
            },
            title = { Text("Welcome to HogWatch!") },
            text = {
                Column {
                    // Image banner
                    Image(
                        painter = painterResource(id = R.drawable.sleephog), // Replace with your image name
                        contentDescription = "Hedgehog Pic",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Please be respectful of the hedgehogs and do under no circumstances disturb them in order to get a good photo!")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showStartupDialog = false
                    }
                ) {
                    Text("I Understand")
                }
            }
        )
    }
}
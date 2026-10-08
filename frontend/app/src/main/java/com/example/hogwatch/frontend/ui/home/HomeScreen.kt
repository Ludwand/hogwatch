package com.example.hogwatch.frontend.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
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
package com.example.hogwatch.frontend

//import android.app.AlertDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.hogwatch.frontend.ui.theme.SrcTheme
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hogwatch.frontend.data.remote.NetworkClient
//import com.example.hogwatch.frontend.ui.camerasubmission.CameraScreen
import com.example.hogwatch.frontend.ui.map.MapScreen
import com.example.hogwatch.frontend.data.repository.UserManager
import com.example.hogwatch.frontend.ui.manualsubmission.ManualScreen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserManager.getInstance(applicationContext)
        enableEdgeToEdge()
        setContent {
            SrcTheme {
                val navController = rememberNavController()

                // Tab Switching
                NavHost(
                    navController = navController,
                    startDestination = "home"
                ) {
                    composable("home") {
                        HomeScreen(navController = navController)
                    }
                    composable("CameraScreen") {
                        //CameraScreen()
                    }
                    composable("ManualScreen") {
                        ManualScreen(navController = navController)
                    }
                    composable("MapScreen") {
                        MapScreen()
                    }
                }
            }
        }
        NetworkClient.initClient()
    }
    //onStart()
    //onResume()
    //onStop()
    //onPause()

    override fun onDestroy() {
        super.onDestroy()
        NetworkClient.closeClient()
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    val userId by UserManager.getUserId()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 3. Debug Text at the top
            Text(
                text = "Debug UUID: ${userId ?: "Loading..."}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Green
            )


            Spacer(modifier = Modifier.height(300.dp))

            CameraButton(navController = navController)

            Spacer(modifier = Modifier.height(50.dp))

            ManualButton(navController = navController)

            Spacer(modifier = Modifier.height(50.dp))

            MapButton(navController = navController)

            Spacer(modifier = Modifier.height(50.dp))

            InfoButton()

        }
    }
}

@Composable
fun CameraButton(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            navController.navigate("CameraScreen")
                  },
        modifier = modifier
            .width(240.dp)
            .height(80.dp)
    ) {
        Text(text = "Submit a photo")
    }
}

@Composable
fun ManualButton(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { navController.navigate("ManualScreen") },
        modifier = modifier
            .width(240.dp)
            .height(80.dp)
    ) {
        Text(text = "Manual Entry")
    }
}

@Composable
fun MapButton(
    navController: NavController,
    modifier: Modifier = Modifier
    ) {
    Button(
        onClick = { navController.navigate("MapScreen") },
        modifier = modifier
            .width(240.dp)
            .height(80.dp)
    ) {
        Text(text = "Map Screen")
    }
}
@Composable
fun InfoButton(modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(false) }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .width(240.dp)
                .height(80.dp)
        ) {
            Text(text = "Click Me")
        }

        if (showDialog) {
            HedgehogInfoPopup(onDismiss = { showDialog = false }) //varför får jag inte kalla en funktion direkt i min onClick lambda?!?!?!?!?!?
        }
    }


@Composable
fun HedgehogInfoPopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Respect the Hedgehog") },
        text = { Text(text = "Do not attempt to move or wake up a hedgehog to get a better photo") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("I Understand")
            }
        },
        /*dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }*/
    )
}
package com.example.hogwatch.frontend

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

import com.example.hogwatch.frontend.theme.SrcTheme
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
                        CameraScreen()
                    }
                    composable("ManualScreen") {
                        ManualScreen(navController = navController)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current

    // Grab singleton instance & store
    val userManager = remember { UserManager.getInstance(context) }

    // Read uuid from stream
    val userId: String? by userManager.userId.collectAsState()
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
        }
    }
}

@Composable
fun CameraButton(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { navController.navigate("CameraScreen") },
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
/* Function for pop-up
@Composable
fun HedgehogInfoPopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Jag hatar Kotlin") },
        text = { Text(text = "Jag vill inte leka med Kotlin längre") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
} */
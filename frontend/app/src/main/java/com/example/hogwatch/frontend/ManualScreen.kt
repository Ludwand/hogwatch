package com.example.hogwatch.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualScreen(navController: NavController) {
    var statusText by remember { mutableStateOf("") }

    // State to control date picker visibility and state
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Back button in the top left
            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Text("Back to Home")
            }

            // Centered content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Manual Submit",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Button 1: Location
                Button(
                    onClick = { statusText = "Clicked Location" },
                    modifier = Modifier
                        .width(240.dp)
                        .height(60.dp)
                ) {
                    Text(text = "Location")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button 2: Date (Opens Date Picker Dialog)
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier
                        .width(240.dp)
                        .height(60.dp)
                ) {
                    Text(text = "Date")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button 3: knapp3
                Button(
                    onClick = { statusText = "Clicked knapp3" },
                    modifier = Modifier
                        .width(240.dp)
                        .height(60.dp)
                ) {
                    Text(text = "knapp3")
                }

                // Status message shown when a button or date is selected
                if (statusText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = statusText,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    // Material 3 Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val formattedDate = formatter.format(Date(millis))
                            statusText = "Selected Date: $formattedDate"
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
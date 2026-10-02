package com.example.hogwatch.frontend.ui.manualsubmission

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hogwatch.frontend.data.remote.NetworkClient
import com.example.hogwatch.frontend.data.repository.UserManager
import kotlinx.coroutines.launch
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
                val coroutineScope = rememberCoroutineScope()
                val userId by UserManager.getUserId()
                Button(
                    onClick = {
                        if (userId == null) {return@Button} //TODO: Maybe error message if Id can't be retrieved? Or can a phone send anyway?
                        coroutineScope.launch {

                            val success = NetworkClient.submit(
                                userId!!, //TODO: make this null-safe and remove "!!"
                                57.6282764,
                                11.9030166,
                                System.currentTimeMillis() / 1000L,
                                null)

                            if (success) {
                                //TODO: print that submission succeed
                                println("Successfully submitted")
                            } else {
                                //TODO: print that submission failed
                                println("Failed to submit")
                            }
                        }
                         },
                    modifier = Modifier
                        .width(240.dp)
                        .height(60.dp)
                ) {
                    Text(text = "Submit")
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
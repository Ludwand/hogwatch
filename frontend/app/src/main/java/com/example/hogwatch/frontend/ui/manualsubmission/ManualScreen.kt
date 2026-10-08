package com.example.hogwatch.frontend.ui.manualsubmission

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun ManualSubmitScreen(
    viewModel: ManualViewModel = viewModel(),
    onNavigateBack: () -> Unit
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSubmittedSuccess) {
        if (uiState.isSubmittedSuccess) {
            onNavigateBack()
        }
    }

    ManualSubmitContent(
        uiState = uiState,
        onLatitudeChange = viewModel::onLatitudeChange,
        onLongitudeChange = viewModel::onLongitudeChange,
        onDateChange = viewModel::onDateChange,
        onTimeChange = viewModel::onTimeChange,
        onFetchLocation = viewModel::fetchUserLocation,
        onSubmit = viewModel::onSubmit
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualSubmitContent(
    uiState: ManualReportUiState,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onFetchLocation: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(is24Hour = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Submit a hedgehog Sighting") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            //Date
            //https://stackoverflow.com/questions/67111020/exposed-drop-down-menu-for-jetpack-compose
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = { showDatePicker = true }
            ) {
                OutlinedTextField(
                    value = uiState.date,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Date") },
                    trailingIcon = {

                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
            }

            //Time
            ExposedDropdownMenuBox(
                expanded = false,
                onExpandedChange = { showTimePicker = true }
            ) {
                OutlinedTextField(
                    value = uiState.time,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Time") },
                    trailingIcon = {

                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Select time"
                        )
                    },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
            }


            // Latitud
            OutlinedTextField(
                value = uiState.latitudeString,
                onValueChange = onLatitudeChange,
                label = { Text("Latitude") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Longitud
            OutlinedTextField(
                value = uiState.longitudeString,
                onValueChange = onLongitudeChange,
                label = { Text("Longitude") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onFetchLocation
            ) {
                Text("Use my location")
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Submit
            Button(
                onClick = onSubmit,
                enabled = !uiState.isSubmitting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                    )
                } else {
                    Text("Submit Sighting")
                }
                //TODO: When connection is refused isSubmittedSuccess seems to become true
            }
        }
    }

    //https://stackoverflow.com/questions/77676317/how-to-disable-future-dates-in-android-compose-material3-datepicker
    //TODO: Disable future dates
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
                            onDateChange(formattedDate)
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

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                        val formattedTime = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        onTimeChange(formattedTime)
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}
//https://developer.android.com/develop/ui/compose/components/time-pickers
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = { content() }
    )
}
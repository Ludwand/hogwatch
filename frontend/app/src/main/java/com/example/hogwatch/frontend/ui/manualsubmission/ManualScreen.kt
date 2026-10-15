package com.example.hogwatch.frontend.ui.manualsubmission

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hogwatch.frontend.ui.location.rememberLocationPermission
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale


@Composable
fun ManualSubmitScreen(
   viewModel: ManualViewModel = viewModel(),
   onNavigateBack: () -> Unit
) {
   val uiState by viewModel.uiState.collectAsStateWithLifecycle()

   LaunchedEffect(uiState.isSubmittedSuccess) {
      if (uiState.isSubmittedSuccess) {
         onNavigateBack()
      }
   }

   val permission = rememberLocationPermission(
      onPermissionGranted = viewModel::fetchUserLocation
   )

   ManualSubmitContent(
      uiState = uiState,
      onLatitudeChange = viewModel::onLatitudeChange,
      onLongitudeChange = viewModel::onLongitudeChange,
      onDateChange = viewModel::onDateChange,
      onTimeChange = viewModel::onTimeChange,
      onFetchLocation = permission.requestPermission,
      onSubmit = viewModel::onSubmit,
      onRadioButton = viewModel::onRadioButton,
      onAddPhoto = viewModel::onAddPhoto
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
   modifier: Modifier = Modifier,
   onRadioButton: (String) -> Unit,
   onAddPhoto: (String) -> Unit
) {
   var showDatePicker by remember { mutableStateOf(false) }
   val datePickerState = rememberDatePickerState(
      yearRange = NoFutureDates.yearRange,
      selectableDates = NoFutureDates
   )

   var showTimePicker by remember { mutableStateOf(false) }
   val timePickerState = rememberTimePickerState(is24Hour = true)

   Scaffold(
      topBar = {
         TopAppBar(
            title = { Text("Submit a hedgehog Sighting") }
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

         Button(
            onClick = {  }
         ) {
            Text("Add photo")
         }





         //https://developer.android.com/develop/ui/compose/components/radio-button
         Column(modifier.selectableGroup()) {
            uiState.radioOptions.forEach { text ->
               Row(
                  Modifier
                     .fillMaxWidth()
                     .height(56.dp)
                     .selectable(
                        selected = (text == uiState.isAlive),
                        onClick = { onRadioButton(text)},
                        role = Role.RadioButton
                     )
                     .padding(horizontal = 16.dp),
                  verticalAlignment = Alignment.CenterVertically
               ) {
                  RadioButton(
                     selected = (text == uiState.isAlive),
                     onClick = null // null recommended for accessibility with screen readers
                  )
                  Text(
                     text = text,
                     style = MaterialTheme.typography.bodyLarge,
                     modifier = Modifier.padding(start = 16.dp)
                  )
               }
            }
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
         }
      }
   }

   //https://stackoverflow.com/questions/77676317/how-to-disable-future-dates-in-android-compose-material3-datepicker
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

// Source - https://stackoverflow.com/a/79754991
// Posted by BennyG
// Retrieved 2026-10-14, License - CC BY-SA 4.0
//Modified to fit our application
@OptIn(ExperimentalMaterial3Api::class)
object NoFutureDates : SelectableDates {
   private val today = LocalDate.now()
   private val todayMillis = today.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
   val yearRange = IntRange(1900, today.year)

   override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= todayMillis
   override fun isSelectableYear(year: Int) = year in yearRange
}
package com.mstoyanov.musiclessons.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TimePicker(
    timeFrom: LocalTime,
    timeTo: LocalTime,
    onTimeFromSelect: (LocalTime) -> Unit,
    onTimeToSelect: (LocalTime) -> Unit
) {
    var timeFromPickerState = rememberTimePickerState(
        is24Hour = true,
        initialHour = timeFrom.hour,
        initialMinute = timeFrom.minute
    )
    var timeToPickerState = rememberTimePickerState(
        is24Hour = true,
        initialHour = timeTo.hour,
        initialMinute = timeTo.minute
    )
    var selectedTimeFrom by rememberSaveable { mutableStateOf(formattedTime(timeFrom.hour, timeFrom.minute)) }
    var selectedTimeTo by rememberSaveable { mutableStateOf(formattedTime(timeTo.hour, timeTo.minute)) }
    var showTimeFromPicker by rememberSaveable { mutableStateOf(false) }
    var showTimeToPicker by rememberSaveable { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth()
                .clickable(onClick = { showTimeFromPicker = true }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(start = 12.dp),
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = Color.Blue
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "Time From: $selectedTimeFrom",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Row(
            modifier = Modifier
                .height(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth()
                .clickable(onClick = { showTimeToPicker = true }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.padding(start = 12.dp),
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = Color.Blue
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text = "Time To: $selectedTimeTo",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }

    if (showTimeFromPicker) {
        TimePickerDialog(timeFromPickerState, onConfirm = {
            timeFromPickerState = it

            val timeFrom = LocalTime.of(timeFromPickerState.hour, timeFromPickerState.minute)
            var timeTo = LocalTime.of(timeToPickerState.hour, timeToPickerState.minute)
            if (timeFrom.isAfter(timeTo)) {
                timeTo = timeFrom.plusMinutes(30)
                timeToPickerState.hour = timeTo.hour
                timeToPickerState.minute = timeTo.minute
                selectedTimeTo = formattedTime(timeToPickerState.hour, timeToPickerState.minute)
                onTimeToSelect(timeTo)
            }

            selectedTimeFrom = formattedTime(timeFromPickerState.hour, timeFromPickerState.minute)
            onTimeFromSelect(timeFrom)
            showTimeFromPicker = false
        }, onDismiss = {
            showTimeFromPicker = false
        })
    }

    if (showTimeToPicker) {
        TimePickerDialog(timeToPickerState, onConfirm = {
            timeToPickerState = it

            var timeFrom = LocalTime.of(timeFromPickerState.hour, timeFromPickerState.minute)
            val timeTo = LocalTime.of(timeToPickerState.hour, timeToPickerState.minute)
            if (timeTo.isBefore(timeFrom)) {
                timeFrom = timeTo.minusMinutes(30)
                timeFromPickerState.hour = timeFrom.hour
                timeFromPickerState.minute = timeFrom.minute
                selectedTimeFrom = formattedTime(timeFromPickerState.hour, timeFromPickerState.minute)
                onTimeFromSelect(timeFrom)
            }

            selectedTimeTo = formattedTime(timeToPickerState.hour, timeToPickerState.minute)
            onTimeToSelect(timeTo)
            showTimeToPicker = false
        }, onDismiss = {
            showTimeToPicker = false
        })
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TimePickerDialog(
    timePickerState: TimePickerState,
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(timePickerState) }) {
                Text(
                    text = "OK",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1B5E20)
                )
            }
        },
        text = { TimePicker(state = timePickerState) }
    )
}

private fun formattedTime(hour: Int, minute: Int): String {
    return LocalTime
        .of(hour, minute)
        .format(DateTimeFormatter.ofPattern("HH:mm"))
}

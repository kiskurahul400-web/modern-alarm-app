package com.example.modernalarm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modernalarm.model.AlarmItem
import com.example.modernalarm.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlarmSheet(
    onDismiss: () -> Unit,
    onSave: (AlarmItem) -> Unit
) {
    val cal = Calendar.getInstance()
    var selectedHour by remember { mutableIntStateOf(cal.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableIntStateOf((cal.get(Calendar.MINUTE) + 5) % 60) }
    var label by remember { mutableStateOf("New Alarm") }
    var vibrate by remember { mutableStateOf(true) }
    val selectedDays = remember { mutableStateListOf<Int>() }

    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

    val timePickerState = rememberTimePickerState(
        initialHour = selectedHour,
        initialMinute = selectedMinute,
        is24Hour = false
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextMuted) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Set Alarm Time",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Modern Material 3 TimePicker
            TimePicker(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor = DarkSurfaceVariant,
                    clockDialSelectedContentColor = Color.Black,
                    clockDialUnselectedContentColor = TextPrimary,
                    selectorColor = NeonCyan,
                    periodSelectorBorderColor = DarkSurfaceVariant,
                    periodSelectorSelectedContainerColor = NeonCyan,
                    periodSelectorUnselectedContainerColor = DarkSurfaceVariant,
                    periodSelectorSelectedContentColor = Color.Black,
                    periodSelectorUnselectedContentColor = TextPrimary,
                    timeSelectorSelectedContainerColor = NeonCyan,
                    timeSelectorUnselectedContainerColor = DarkSurfaceVariant,
                    timeSelectorSelectedContentColor = Color.Black,
                    timeSelectorUnselectedContentColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Day Selector Pills
            Text(
                text = "Repeat on days",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dayLabels.forEachIndexed { index, name ->
                    val dayNum = index + 1
                    val isSelected = selectedDays.contains(dayNum)

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) NeonCyan else DarkSurfaceVariant)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) NeonCyan else DarkSurfaceVariant,
                                shape = CircleShape
                            )
                            .clickable {
                                if (isSelected) selectedDays.remove(dayNum) else selectedDays.add(dayNum)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isSelected) Color.Black else TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Alarm Label TextField
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Label", color = TextSecondary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = DarkSurfaceVariant,
                    focusedContainerColor = DarkSurfaceVariant,
                    unfocusedContainerColor = DarkSurfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Vibrate Toggle Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vibration",
                    fontSize = 15.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Switch(
                    checked = vibrate,
                    onCheckedChange = { vibrate = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = NeonCyan
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Save & Cancel Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                ) {
                    Text("Cancel", fontSize = 16.sp)
                }

                Button(
                    onClick = {
                        val newAlarm = AlarmItem(
                            hour = timePickerState.hour,
                            minute = timePickerState.minute,
                            label = if (label.isBlank()) "Alarm" else label,
                            daysOfWeek = selectedDays.toList(),
                            isVibrate = vibrate,
                            isEnabled = true
                        )
                        onSave(newAlarm)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Save", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

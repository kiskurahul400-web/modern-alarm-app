package com.example.modernalarm.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modernalarm.model.AlarmItem
import com.example.modernalarm.ui.theme.*

@Composable
fun AlarmCard(
    alarm: AlarmItem,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor by animateColorAsState(
        targetValue = if (alarm.isEnabled) DarkSurfaceVariant else DarkSurface.copy(alpha = 0.6f),
        label = "cardColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (alarm.isEnabled) TextPrimary else TextMuted,
        label = "textColor"
    )
    val timeColor by animateColorAsState(
        targetValue = if (alarm.isEnabled) NeonCyan else TextMuted,
        label = "timeColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (alarm.isEnabled) 6.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time & AM/PM
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = alarm.formattedTime,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = timeColor,
                        letterSpacing = (-1).sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = alarm.amPm,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (alarm.isEnabled) SoftPurple else TextMuted,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Switch Toggle
                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = NeonCyan,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = DarkSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Label & Days Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = alarm.label,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = alarm.daysSummary,
                        fontSize = 13.sp,
                        color = if (alarm.isEnabled) TextSecondary else TextMuted
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Alarm",
                        tint = NeonCoral.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

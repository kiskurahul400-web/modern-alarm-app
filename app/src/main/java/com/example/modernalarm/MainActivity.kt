package com.example.modernalarm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.modernalarm.data.AlarmRepository
import com.example.modernalarm.model.AlarmItem
import com.example.modernalarm.receiver.AlarmScheduler
import com.example.modernalarm.ui.components.AddAlarmSheet
import com.example.modernalarm.ui.components.AlarmCard
import com.example.modernalarm.ui.theme.*
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {

    private lateinit var repository: AlarmRepository
    private lateinit var scheduler: AlarmScheduler

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Permission handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = AlarmRepository(this)
        scheduler = AlarmScheduler(this)

        checkPermissions()

        setContent {
            ModernAlarmTheme {
                MainScreen(
                    repository = repository,
                    scheduler = scheduler
                )
            }
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun MainScreen(
    repository: AlarmRepository,
    scheduler: AlarmScheduler
) {
    val alarms by repository.alarms.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }

    // Live clock updater
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now()
            delay(1000)
        }
    }

    val activeAlarmsCount = alarms.count { it.isEnabled }

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = NeonCyan,
                contentColor = Color.Black,
                shape = CircleShape,
                modifier = Modifier
                    .size(64.dp)
                    .offset(y = (-8).dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Alarm",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Alarms",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (activeAlarmsCount > 0) "$activeAlarmsCount active alarms" else "No active alarms",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                // Glowing Clock Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = currentTime.format(DateTimeFormatter.ofPattern("hh:mm:ss a")),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Upcoming Alarm Banner
            val nextAlarm = alarms.filter { it.isEnabled }.minByOrNull {
                val hourDiff = (it.hour - currentTime.hour + 24) % 24
                val minDiff = (it.minute - currentTime.minute + 60) % 60
                hourDiff * 60 + minDiff
            }

            if (nextAlarm != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    SoftPurple.copy(alpha = 0.25f),
                                    NeonCyan.copy(alpha = 0.25f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Next Alarm: ${nextAlarm.formattedTime} ${nextAlarm.amPm}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = nextAlarm.label,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Alarms List
            if (alarms.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No alarms set.\nTap '+' to create one.",
                        fontSize = 16.sp,
                        color = TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(
                        items = alarms,
                        key = { it.id }
                    ) { alarm ->
                        AlarmCard(
                            alarm = alarm,
                            onToggle = { enabled ->
                                val updated = repository.toggleAlarm(alarm.id, enabled)
                                if (updated != null) {
                                    if (enabled) scheduler.schedule(updated) else scheduler.cancel(alarm)
                                }
                            },
                            onDelete = {
                                scheduler.cancel(alarm)
                                repository.deleteAlarm(alarm.id)
                            }
                        )
                    }
                }
            }
        }

        // Add Alarm BottomSheet
        if (showAddSheet) {
            AddAlarmSheet(
                onDismiss = { showAddSheet = false },
                onSave = { newAlarm ->
                    repository.addOrUpdateAlarm(newAlarm)
                    scheduler.schedule(newAlarm)
                    showAddSheet = false
                }
            )
        }
    }
}

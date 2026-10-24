package com.example.modernalarm

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.modernalarm.service.AlarmService
import com.example.modernalarm.ui.theme.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AlarmTriggerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wakeAndUnlock()

        val label = intent.getStringExtra("EXTRA_ALARM_LABEL") ?: "Wake Up!"

        setContent {
            ModernAlarmTheme {
                AlarmRingingScreen(
                    label = label,
                    onDismiss = {
                        stopAlarmService(AlarmService.ACTION_DISMISS)
                        finish()
                    },
                    onSnooze = {
                        stopAlarmService(AlarmService.ACTION_SNOOZE)
                        finish()
                    }
                )
            }
        }
    }

    private fun stopAlarmService(action: String) {
        val serviceIntent = Intent(this, AlarmService::class.java).apply {
            this.action = action
        }
        startService(serviceIntent)
    }

    private fun wakeAndUnlock() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }
}

@Composable
fun AlarmRingingScreen(
    label: String,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit
) {
    val currentTime = remember { LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")) }

    // Pulsing glowing ring animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Time & Alarm Label
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = label,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentTime,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeonCyan,
                    letterSpacing = (-1).sp
                )
            }

            // Pulsing Alarm Icon
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.15f))
                        .border(2.dp, NeonCyan.copy(alpha = 0.4f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = "Ringing Alarm",
                        tint = NeonCyan,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Action Buttons (Dismiss & Snooze)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCoral,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Dismiss Alarm",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onSnooze,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceVariant)
                ) {
                    Text(
                        text = "Snooze (5 min)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

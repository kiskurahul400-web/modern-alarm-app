package com.example.modernalarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.modernalarm.AlarmTriggerActivity
import com.example.modernalarm.data.AlarmRepository
import com.example.modernalarm.service.AlarmService

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getStringExtra("EXTRA_ALARM_ID") ?: ""
        val label = intent.getStringExtra("EXTRA_ALARM_LABEL") ?: "Alarm"
        val vibrate = intent.getBooleanExtra("EXTRA_ALARM_VIBRATE", true)

        // Start background ringtone / vibration service
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("EXTRA_ALARM_ID", alarmId)
            putExtra("EXTRA_ALARM_LABEL", label)
            putExtra("EXTRA_ALARM_VIBRATE", vibrate)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        // Launch full-screen alarm screen
        val activityIntent = Intent(context, AlarmTriggerActivity::class.java).apply {
            putExtra("EXTRA_ALARM_ID", alarmId)
            putExtra("EXTRA_ALARM_LABEL", label)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(activityIntent)

        // Reschedule if repeating, or disable if single shot
        val repo = AlarmRepository(context)
        val alarm = repo.getAlarmById(alarmId)
        if (alarm != null) {
            if (alarm.daysOfWeek.isEmpty()) {
                repo.toggleAlarm(alarmId, false)
            } else {
                AlarmScheduler(context).schedule(alarm)
            }
        }
    }
}

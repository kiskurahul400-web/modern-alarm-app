package com.example.modernalarm.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.modernalarm.model.AlarmItem
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(alarm: AlarmItem) {
        if (!alarm.isEnabled) return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_ALARM_ID", alarm.id)
            putExtra("EXTRA_ALARM_LABEL", alarm.label)
            putExtra("EXTRA_ALARM_VIBRATE", alarm.isVibrate)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = calculateNextTriggerTime(alarm)

        try {
            // AlarmClockInfo gives exact wake-up even during Doze mode and shows alarm icon in status bar
            val showIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            val showPendingIntent = PendingIntent.getActivity(
                context,
                0,
                showIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, showPendingIntent)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
            Log.d("AlarmScheduler", "Alarm scheduled for ${alarm.label} at $triggerTime")
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Exact alarm permission missing", e)
        }
    }

    fun cancel(alarm: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun calculateNextTriggerTime(alarm: AlarmItem): Long {
        val now = Calendar.getInstance()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (alarm.daysOfWeek.isEmpty()) {
            // Once: if time passed today, schedule for tomorrow
            if (calendar.before(now) || calendar.timeInMillis <= now.timeInMillis) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        } else {
            // Repeating days: 1=Mon...7=Sun -> Calendar: 2=Mon...1=Sun
            // Find next matching day
            var daysToAdd = 0
            while (daysToAdd < 7) {
                val checkCal = calendar.clone() as Calendar
                checkCal.add(Calendar.DAY_OF_YEAR, daysToAdd)
                val calDay = checkCal.get(Calendar.DAY_OF_WEEK)
                val dayOfWeek = if (calDay == Calendar.SUNDAY) 7 else calDay - 1

                if (alarm.daysOfWeek.contains(dayOfWeek)) {
                    if (checkCal.timeInMillis > now.timeInMillis) {
                        return checkCal.timeInMillis
                    }
                }
                daysToAdd++
            }
            // Fallback: 7 days later
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }

        return calendar.timeInMillis
    }
}

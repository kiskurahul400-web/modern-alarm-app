package com.example.modernalarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.modernalarm.data.AlarmRepository

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val repo = AlarmRepository(context)
            val scheduler = AlarmScheduler(context)
            val alarms = repo.alarms.value

            alarms.filter { it.isEnabled }.forEach { alarm ->
                scheduler.schedule(alarm)
            }
        }
    }
}

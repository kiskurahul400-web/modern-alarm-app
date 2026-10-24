package com.example.modernalarm.data

import android.content.Context
import android.content.SharedPreferences
import com.example.modernalarm.model.AlarmItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AlarmRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("modern_alarm_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val _alarms = MutableStateFlow<List<AlarmItem>>(emptyList())
    val alarms: StateFlow<List<AlarmItem>> = _alarms.asStateFlow()

    init {
        loadAlarms()
    }

    private fun loadAlarms() {
        val json = prefs.getString("alarms_list", null)
        if (json != null) {
            val type = object : TypeToken<List<AlarmItem>>() {}.type
            val list: List<AlarmItem> = gson.fromJson(json, type) ?: emptyList()
            _alarms.value = list.sortedWith(compareBy({ it.hour }, { it.minute }))
        } else {
            // Default sample alarms
            val defaultList = listOf(
                AlarmItem(hour = 7, minute = 0, label = "Morning Workout", daysOfWeek = listOf(1, 2, 3, 4, 5)),
                AlarmItem(hour = 8, minute = 30, label = "Work Routine", daysOfWeek = listOf(1, 2, 3, 4, 5))
            )
            saveAlarms(defaultList)
        }
    }

    private fun saveAlarms(list: List<AlarmItem>) {
        val sorted = list.sortedWith(compareBy({ it.hour }, { it.minute }))
        _alarms.value = sorted
        val json = gson.toJson(sorted)
        prefs.edit().putString("alarms_list", json).apply()
    }

    fun addOrUpdateAlarm(alarm: AlarmItem) {
        val current = _alarms.value.toMutableList()
        val index = current.indexOfFirst { it.id == alarm.id }
        if (index >= 0) {
            current[index] = alarm
        } else {
            current.add(alarm)
        }
        saveAlarms(current)
    }

    fun toggleAlarm(id: String, enabled: Boolean): AlarmItem? {
        val current = _alarms.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        return if (index >= 0) {
            val updated = current[index].copy(isEnabled = enabled)
            current[index] = updated
            saveAlarms(current)
            updated
        } else null
    }

    fun deleteAlarm(id: String) {
        val current = _alarms.value.filter { it.id != id }
        saveAlarms(current)
    }

    fun getAlarmById(id: String): AlarmItem? {
        return _alarms.value.find { it.id == id }
    }
}

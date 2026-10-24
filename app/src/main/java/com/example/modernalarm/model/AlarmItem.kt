package com.example.modernalarm.model

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class AlarmItem(
    val id: String = UUID.randomUUID().toString(),
    val hour: Int,
    val minute: Int,
    val label: String = "Alarm",
    val isEnabled: Boolean = true,
    val daysOfWeek: List<Int> = emptyList(), // 1 = Monday ... 7 = Sunday
    val isVibrate: Boolean = true
) {
    val formattedTime: String
        get() {
            val time = LocalTime.of(hour, minute)
            return time.format(DateTimeFormatter.ofPattern("hh:mm"))
        }

    val amPm: String
        get() = if (hour < 12) "AM" else "PM"

    val daysSummary: String
        get() {
            if (daysOfWeek.isEmpty()) return "Ring once"
            if (daysOfWeek.size == 7) return "Every day"
            if (daysOfWeek.sorted() == listOf(1, 2, 3, 4, 5)) return "Weekdays"
            if (daysOfWeek.sorted() == listOf(6, 7)) return "Weekends"
            val names = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            return daysOfWeek.sorted().mapNotNull { if (it in 1..7) names[it - 1] else null }.joinToString(" • ")
        }
}

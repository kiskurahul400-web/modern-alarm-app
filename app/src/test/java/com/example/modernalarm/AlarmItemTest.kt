package com.example.modernalarm

import com.example.modernalarm.model.AlarmItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlarmItemTest {

    @Test
    fun testTimeFormattingMorning() {
        val alarm = AlarmItem(hour = 7, minute = 5, label = "Morning")
        assertEquals("07:05", alarm.formattedTime)
        assertEquals("AM", alarm.amPm)
    }

    @Test
    fun testTimeFormattingAfternoon() {
        val alarm = AlarmItem(hour = 14, minute = 30, label = "Meeting")
        assertEquals("02:30", alarm.formattedTime)
        assertEquals("PM", alarm.amPm)
    }

    @Test
    fun testTimeFormattingMidnight() {
        val alarm = AlarmItem(hour = 0, minute = 0, label = "Midnight")
        assertEquals("12:00", alarm.formattedTime)
        assertEquals("AM", alarm.amPm)
    }

    @Test
    fun testDaysSummaryOnce() {
        val alarm = AlarmItem(hour = 8, minute = 0, daysOfWeek = emptyList())
        assertEquals("Ring once", alarm.daysSummary)
    }

    @Test
    fun testDaysSummaryEveryDay() {
        val alarm = AlarmItem(hour = 8, minute = 0, daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7))
        assertEquals("Every day", alarm.daysSummary)
    }

    @Test
    fun testDaysSummaryWeekdays() {
        val alarm = AlarmItem(hour = 8, minute = 0, daysOfWeek = listOf(1, 2, 3, 4, 5))
        assertEquals("Weekdays", alarm.daysSummary)
    }

    @Test
    fun testDaysSummaryWeekends() {
        val alarm = AlarmItem(hour = 9, minute = 0, daysOfWeek = listOf(6, 7))
        assertEquals("Weekends", alarm.daysSummary)
    }

    @Test
    fun testDaysSummaryCustomDays() {
        val alarm = AlarmItem(hour = 10, minute = 0, daysOfWeek = listOf(1, 3, 5))
        assertEquals("Mon • Wed • Fri", alarm.daysSummary)
    }

    @Test
    fun testAlarmToggleCopy() {
        val alarm = AlarmItem(hour = 7, minute = 0, isEnabled = true)
        val toggled = alarm.copy(isEnabled = false)
        assertEquals(false, toggled.isEnabled)
        assertEquals(alarm.id, toggled.id)
    }
}

package com.anafthdev.aquri.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

class DateTimeUtilsTest {

    private val zoneId = ZoneId.systemDefault()

    @Test
    fun getMidnight_isCorrect() {
        // Thursday, April 9, 2026 12:34:56 UTC
        val timestamp = 1775738096000L 
        val midnight = DateTimeUtils.getMidnight(timestamp)
        
        val midnightDateTime = Instant.ofEpochMilli(midnight).atZone(zoneId)
        assertEquals(0, midnightDateTime.hour)
        assertEquals(0, midnightDateTime.minute)
        assertEquals(0, midnightDateTime.second)
    }

    @Test
    fun getWeekRange_isCorrect() {
        // Thursday, April 9, 2026 (1775738096000L)
        // Week: Monday, April 6, 2026 to Sunday, April 12, 2026
        val timestamp = 1775738096000L 
        val (start, end) = DateTimeUtils.getWeekRange(timestamp)
        
        val startDateTime = Instant.ofEpochMilli(start).atZone(zoneId)
        val endDateTime = Instant.ofEpochMilli(end).atZone(zoneId)
        
        assertEquals(6, startDateTime.dayOfMonth)
        assertEquals(4, startDateTime.monthValue)
        assertEquals(2026, startDateTime.year)
        assertEquals(0, startDateTime.hour)
        
        assertEquals(12, endDateTime.dayOfMonth)
        assertEquals(4, endDateTime.monthValue)
        assertEquals(2026, endDateTime.year)
        assertEquals(0, endDateTime.hour)
    }

    @Test
    fun isSameDay_isCorrect() {
        val t1 = 1775738096000L // April 9, 2026 12:34:56
        val t2 = 1775738096000L + 3600000L // April 9, 2026 13:34:56
        val t3 = 1775738096000L + 24 * 3600000L // April 10, 2026 12:34:56
        
        assertTrue(DateTimeUtils.isSameDay(t1, t2))
        assertFalse(DateTimeUtils.isSameDay(t1, t3))
    }
}

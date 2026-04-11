package com.anafthdev.aquri.utils

import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

/**
 * Utility class for date and time calculations.
 * Centralizes date logic to ensure consistency across the application.
 */
object DateTimeUtils {
    private val zoneId = ZoneId.systemDefault()

    /**
     * Returns the midnight timestamp (00:00:00.000) for the given timestamp.
     */
    fun getMidnight(timestamp: Long): Long {
        return Instant.ofEpochMilli(timestamp)
            .atZone(zoneId)
            .toLocalDate()
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
    }

    /**
     * Returns the start and end midnight timestamps for the week containing the given timestamp.
     * The week is assumed to start on Monday and end on Sunday.
     */
    fun getWeekRange(timestamp: Long): Pair<Long, Long> {
        val date = Instant.ofEpochMilli(timestamp)
            .atZone(zoneId)
            .toLocalDate()
        
        val start = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
            
        val end = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
            
        return start to end
    }

    /**
     * Checks if two timestamps represent the same day in the system default timezone.
     */
    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val date1 = Instant.ofEpochMilli(timestamp1).atZone(zoneId).toLocalDate()
        val date2 = Instant.ofEpochMilli(timestamp2).atZone(zoneId).toLocalDate()
        return date1 == date2
    }
}

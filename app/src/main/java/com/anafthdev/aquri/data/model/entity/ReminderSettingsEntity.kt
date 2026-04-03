package com.anafthdev.aquri.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.anafthdev.aquri.data.model.enum.FrequencyType
import java.util.UUID

@Entity(tableName = "reminder_settings")
data class ReminderSettingsEntity(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "user_id")
    val userId: UUID,
    val enabled: Boolean = true,
    @ColumnInfo(name = "schedule_times")
    val scheduleTimes: List<String> = emptyList(),
    @ColumnInfo(name = "frequency_type")
    val frequencyType: FrequencyType = FrequencyType.Interval,
    @ColumnInfo(name = "interval_minutes")
    val intervalMinutes: Int = 60,
    @ColumnInfo(name = "smart_reminders")
    val smartReminders: Boolean = false
)

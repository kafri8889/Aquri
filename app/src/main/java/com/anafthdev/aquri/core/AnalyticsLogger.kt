package com.anafthdev.aquri.core

import android.os.Bundle
import timber.log.Timber

object AnalyticsLogger {

    fun logDrink(amount: Int, type: String, source: String) {
        val bundle = Bundle().apply {
            putInt("amount_ml", amount)
            putString("drink_type", type)
            putString("source", source)
        }

        logEvent("drink_recorded", bundle)
    }

    fun logReminderClicked(delay: Long) {
        val bundle = Bundle().apply {
            putLong("delay_seconds", delay)
        }

        logEvent("reminder_clicked", bundle)
    }

    fun logMissionCompleted(type: String, id: String) {
        val bundle = Bundle().apply {
            putString("mission_type", type)
            putString("mission_id", id)
        }

        logEvent("mission_completed", bundle)
    }

    fun logXp(amount: Int, source: String) {
        val bundle = Bundle().apply {
            putInt("amount", amount)
            putString("source", source)
        }

        logEvent("xp_earned", bundle)
    }

    fun logEvent(name: String, bundle: Bundle) {
        Timber.d("Event: $name -> $bundle")
//        Firebase.analytics.logEvent(name, bundle)
    }
}

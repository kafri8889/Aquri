package com.anafthdev.aquri

import android.app.Application
import com.anafthdev.aquri.data.database.DatabaseInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class AquriApplication : Application() {

    @Inject
    lateinit var databaseInitializer: DatabaseInitializer

    override fun onCreate() {
        super.onCreate()
        MainScope().launch {
            databaseInitializer.initialize()
        }
    }
}

package com.anafthdev.aquri

import android.app.Application
import android.content.res.Resources
import com.anafthdev.aquri.data.database.DatabaseInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class AquriApplication : Application() {

    @Inject
    lateinit var databaseInitializer: DatabaseInitializer

    private var _globalResources: Resources? = null
    val globalResources: Resources?
        get() = _globalResources

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        instance = applicationContext as AquriApplication
        _globalResources = instance?.resources

        MainScope().launch {
            databaseInitializer.initialize()
        }
    }

    companion object {

        var instance: AquriApplication? = null
    }
}

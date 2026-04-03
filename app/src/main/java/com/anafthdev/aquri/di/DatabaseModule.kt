package com.anafthdev.aquri.di

import android.content.Context
import androidx.room.Room
import com.anafthdev.aquri.data.database.AquriDatabase
import com.anafthdev.aquri.data.database.dao.BadgeDao
import com.anafthdev.aquri.data.database.dao.HydrationDao
import com.anafthdev.aquri.data.database.dao.MissionDao
import com.anafthdev.aquri.data.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AquriDatabase {
        return Room.databaseBuilder(
            context,
            AquriDatabase::class.java,
            AquriDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideUserDao(db: AquriDatabase): UserDao = db.userDao()

    @Provides
    fun provideHydrationDao(db: AquriDatabase): HydrationDao = db.hydrationDao()

    @Provides
    fun provideMissionDao(db: AquriDatabase): MissionDao = db.missionDao()

    @Provides
    fun provideBadgeDao(db: AquriDatabase): BadgeDao = db.badgeDao()
}

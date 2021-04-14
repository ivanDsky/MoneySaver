package ua.zloyhr.moneysaver.data.db

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object ChargeModule {
    @Provides
    fun provideChargeDao(chargeDatabase: ChargeDatabase) : ChargeDao = chargeDatabase.chargeDao()

    @Provides
    @Singleton
    fun provideChargeDatabase(app: Application,callback: ChargeDatabase.Callback) : ChargeDatabase =
        Room.databaseBuilder(app,ChargeDatabase::class.java,"DatabaseReader")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
}
package ua.zloyhr.moneysaver.data.db

import android.content.Context
import androidx.room.Room
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
    fun provideChargeDatabase(@ApplicationContext appContext : Context) : ChargeDatabase =
        Room.databaseBuilder(appContext,ChargeDatabase::class.java,"DatabaseReader").build()
}
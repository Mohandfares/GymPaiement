package com.dz.mobile.gympaiement.di

import android.app.Application
import androidx.room.Room
import com.dz.mobile.gympaiement.data.GymDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataBase(application: Application) =
        Room.databaseBuilder(application,GymDataBase::class.java,"gymdatabase").build()

    @Provides
    @Singleton
    fun provideAthleteDao(dataBase: GymDataBase) = dataBase.athleteDao()

    @Provides
    @Singleton
    fun providePaymentDao(dataBase: GymDataBase) = dataBase.paymentDao()

}
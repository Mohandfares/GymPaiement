package com.dz.mobile.gympaiement.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dz.mobile.gympaiement.data.bo.Athlete
import com.dz.mobile.gympaiement.data.bo.DateConverter
import com.dz.mobile.gympaiement.data.bo.Payment
import com.dz.mobile.gympaiement.data.dao.AthleteDao
import com.dz.mobile.gympaiement.data.dao.PaymentDao

@Database(entities = [Athlete::class,Payment::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class GymDataBase : RoomDatabase() {

    abstract fun athleteDao(): AthleteDao
    abstract fun paymentDao(): PaymentDao
}
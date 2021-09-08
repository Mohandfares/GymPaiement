package com.dz.mobile.gympaiement.data.bo

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "Athlete")
@Parcelize
data class Athlete(
    val firstAndLastName: String,
    @PrimaryKey(autoGenerate = true)
    val idAthlete: Int = 0
) : Parcelable

@Entity(tableName = "Payment")
@Parcelize
data class Payment(
    val amount: Double,
    val type: String,
    val date: Date?,
    val AthleteId: Int,
    @PrimaryKey(autoGenerate = true)
    val idPayment: Int = 0
) : Parcelable

object DateConverter {

    val format = SimpleDateFormat("yyyy-MM-dd")

    @TypeConverter
    fun toDate(dateString: String?): Date? {
        return dateString?.let { format.parse(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): String? {
        return date?.let { format.format(it) }
    }
}
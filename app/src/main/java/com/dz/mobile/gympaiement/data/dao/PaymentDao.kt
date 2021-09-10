package com.dz.mobile.gympaiement.data.dao

import androidx.room.*
import com.dz.mobile.gympaiement.data.bo.Payment
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface PaymentDao {

    @Query("SELECT * FROM Payment WHERE AthleteId = :AthleteId ORDER BY idPayment DESC")
    fun getAthletePayments(AthleteId: Int): Flow<List<Payment>>

    @Query("SELECT * FROM Payment WHERE AthleteId = :AthleteId AND date = :date AND type = :type LIMIT 1")
    suspend fun getAthletePayment(AthleteId: Int,date: Date,type: String): Payment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(payment: Payment)

    @Update
    suspend fun update(payment: Payment)

    @Delete
    suspend fun delete(payment: Payment)
}
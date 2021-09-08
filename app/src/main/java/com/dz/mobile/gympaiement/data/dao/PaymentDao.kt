package com.dz.mobile.gympaiement.data.dao

import androidx.room.*
import com.dz.mobile.gympaiement.data.bo.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Query("SELECT * FROM Payment WHERE AthleteId = :AthleteId")
    fun getAthletePayments(AthleteId: Int): Flow<List<Payment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(payment: Payment)

    @Update
    suspend fun update(payment: Payment)

    @Delete
    suspend fun delete(payment: Payment)
}
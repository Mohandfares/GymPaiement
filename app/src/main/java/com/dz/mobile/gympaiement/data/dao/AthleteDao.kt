package com.dz.mobile.gympaiement.data.dao

import androidx.room.*
import com.dz.mobile.gympaiement.data.bo.Athlete
import kotlinx.coroutines.flow.Flow

@Dao
interface AthleteDao {

    @Query("SELECT * FROM Athlete WHERE firstAndLastName LIKE '%' || :searchQuery || '%' ORDER BY lastPaymentDate DESC")
    fun getAthletes(searchQuery: String): Flow<List<Athlete>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(athlete: Athlete)

    @Update
    suspend fun update(athlete: Athlete)

    @Delete
    suspend fun delete(athlete: Athlete)

    @Query("SELECT * FROM Athlete ORDER BY idAthlete DESC LIMIT 1")
    suspend fun findLastAthlete(): Athlete
}
package com.dz.mobile.gympaiement.view.ui.detailed

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.dz.mobile.gympaiement.R
import com.dz.mobile.gympaiement.data.bo.Athlete
import com.dz.mobile.gympaiement.data.bo.Payment
import com.dz.mobile.gympaiement.data.dao.AthleteDao
import com.dz.mobile.gympaiement.data.dao.PaymentDao
import com.dz.mobile.gympaiement.view.Ext.toStringFormat
import com.dz.mobile.gympaiement.view.Ext.visible
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailedViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val paymentDao: PaymentDao,
    private val athleteDao: AthleteDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val athlete: Athlete? = savedStateHandle.get<Athlete?>("athlete")
    private val stateFlow = MutableStateFlow(Date())
    val athletePayments = stateFlow.flatMapLatest {
        athlete?.let {
            paymentDao.getAthletePayments(it.idAthlete)
        }!!
    }

    suspend fun save(
        value: String,
        type: String,
        athlete: Athlete
    ): SavePaymentResult {
        if (value.isBlank())
            return SavePaymentResult.ErrorSavePayment(context.getString(R.string.errormontant))
        if (paymentDao.getAthletePayment(athlete.idAthlete,Date(),type) != null)
            return SavePaymentResult.PaymentIsSaved
        val payment = Payment(value.toDouble(),type,Date(),athlete.idAthlete)
        paymentDao.save(payment)
        athleteDao.update(athlete.copy(lastPaymentDate = payment.date))
        stateFlow.value = Date()
        return SavePaymentResult.SaveWithSuccess
    }

    sealed class SavePaymentResult {
        object SaveWithSuccess : SavePaymentResult()
        data class ErrorSavePayment(val error: String) : SavePaymentResult()
        object PaymentIsSaved : SavePaymentResult()
    }
}
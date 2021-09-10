package com.dz.mobile.gympaiement.view.ui.detailed

import android.content.Context
import androidx.lifecycle.ViewModel
import com.dz.mobile.gympaiement.R
import com.dz.mobile.gympaiement.data.bo.Payment
import com.dz.mobile.gympaiement.data.dao.PaymentDao
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailedViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val paymentDao: PaymentDao
) : ViewModel() {

    private val stateFlow = MutableStateFlow(Date())

    fun getAthletePayments(AthleteId: Int) = stateFlow.flatMapLatest {
        paymentDao.getAthletePayments(AthleteId)
    }

    suspend fun save(
        value: String,
        type: String,
        AthleteId: Int
    ): SavePaymentResult {
        if (value.isBlank())
            return SavePaymentResult.ErrorSavePayment(context.getString(R.string.errormontant))
        if (paymentDao.getAthletePayment(AthleteId,Date(),type) != null)
            return SavePaymentResult.PaymentIsSaved
        val payment = Payment(value.toDouble(),type,Date(),AthleteId)
        paymentDao.save(payment)
        stateFlow.value = Date()
        return SavePaymentResult.SaveWithSuccess
    }

    sealed class SavePaymentResult {
        object SaveWithSuccess : SavePaymentResult()
        data class ErrorSavePayment(val error: String) : SavePaymentResult()
        object PaymentIsSaved : SavePaymentResult()
    }
}
package com.dz.mobile.gympaiement.view.ui.detailed

import androidx.lifecycle.ViewModel
import com.dz.mobile.gympaiement.data.bo.Payment
import com.dz.mobile.gympaiement.data.dao.PaymentDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DetailedViewModel @Inject constructor(
    private val paymentDao: PaymentDao
) : ViewModel() {

    fun getAthletePayments(AthleteId: Int): Flow<List<Payment>> =
        paymentDao.getAthletePayments(AthleteId)
}
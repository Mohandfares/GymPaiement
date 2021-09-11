package com.dz.mobile.gympaiement.view.ui.add

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dz.mobile.gympaiement.R
import com.dz.mobile.gympaiement.data.bo.Athlete
import com.dz.mobile.gympaiement.data.bo.Payment
import com.dz.mobile.gympaiement.data.dao.AthleteDao
import com.dz.mobile.gympaiement.data.dao.PaymentDao
import com.dz.mobile.gympaiement.view.Ext.toDateFormat
import com.dz.mobile.gympaiement.view.Ext.toStringFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class AddAthleteViewModel @Inject constructor(
    private val athleteDao: AthleteDao,
    private val paymentDao: PaymentDao,
) : ViewModel() {

    val firstAndLastNameState = MutableStateFlow("")
    val amountState = MutableStateFlow("")
    val dateState = MutableStateFlow(Date())
    private val typePaymentState = MutableStateFlow("Par mois")

    fun firstAndLastNameChange(value: String) {
        firstAndLastNameState.value = value
    }

    fun amountChange(value: String) {
        amountState.value = value
    }

    fun typePaymentChange(value: String) {
        typePaymentState.value = value
    }

    fun dateChange(value: String) {
        dateState.value = value.toDateFormat()
    }

    suspend fun saveAthletePayment() {
        if (firstAndLastNameState.value.isNullOrBlank()) {
            eventsChannel.send(AddEvent.SaveError)
            return
        }
        if (amountState.value.isNullOrBlank() || amountState.value.toInt() == 0)  {
            eventsChannel.send(AddEvent.SaveError)
            return
        }
        if (dateState.value.time  > Date().time && Date().toStringFormat() != dateState.value.toStringFormat()) {
            eventsChannel.send(AddEvent.SaveError)
            return
        }
        athleteDao.save(Athlete(firstAndLastNameState.value,dateState.value))
        val lastAthlete = athleteDao.findLastAthlete()
        val payment = Payment(
            amountState.value.toDouble(),
            typePaymentState.value,
            dateState.value,
            lastAthlete.idAthlete
        )
        paymentDao.save(payment)
        eventsChannel.send(AddEvent.SaveWithSuccess)
    }

    private val eventsChannel = Channel<AddEvent>()
    val events = eventsChannel.receiveAsFlow()

    sealed class AddEvent {
        object SaveWithSuccess : AddEvent()
        object SaveError : AddEvent()
    }
}

package com.dz.mobile.gympaiement.view.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dz.mobile.gympaiement.data.bo.Athlete
import com.dz.mobile.gympaiement.data.dao.AthleteDao
import com.dz.mobile.gympaiement.view.Ext.toStringFormat
import com.dz.mobile.gympaiement.view.Ext.visible
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AthleteViewModel @Inject constructor(
    private val athleteDao: AthleteDao
) : ViewModel() {

    private val queryState = MutableStateFlow("")
    private val refreshState = MutableStateFlow(Date())
    val alertShow = MutableStateFlow(false)

    val athletes = combine(queryState,alertShow,refreshState) {
        query,alert,refresh -> Triple(query,alert,refresh)
    }.flatMapLatest { (query, alert, _) ->
        if (alert) {
            athletesNotPay(query)
        } else {
            athleteDao.getAthletes(query)
        }
    }

    private suspend fun athletesNotPay(query: String): Flow<List<Athlete>> {
        val athletesNotPay = mutableListOf<Athlete>()
        val all = athleteDao.getAthletes(query).first()
        all.filter { it.lastPaymentDate?.toStringFormat() != Date().toStringFormat() }
            .forEach { athlete ->
                val thisDay = Date().toStringFormat().split("-")
                val lastPayment = athlete.lastPaymentDate?.toStringFormat()?.split("-")!!
                if (lastPayment[0] == thisDay[0]) {
                    if (lastPayment[1] != thisDay[1] && thisDay[1].toInt() - lastPayment[1].toInt() == 1) {
                        if (thisDay[2].toInt() - lastPayment[2].toInt() >= 0) {
                            athletesNotPay.add(athlete)
                        }
                    } else if (thisDay[1].toInt() - lastPayment[1].toInt() > 1) {
                        athletesNotPay.add(athlete)
                    }
                } else {
                    athletesNotPay.add(athlete)
                }
            }
        return flowOf(athletesNotPay)
    }

    fun queryChange(query: String) {
        queryState.value = query.replace("'","''")
    }

    fun refresh() {
        refreshState.value = Date()
    }

    fun alertShowStateChange() {
        alertShow.value = !alertShow.value
    }

    private val eventsChannel = Channel<NavigationEvent>()
    val navigationEvents = eventsChannel.receiveAsFlow()

    fun navigationToAdd() = viewModelScope.launch {
        eventsChannel.send(NavigationEvent.NavigationAdd)
    }

    fun navigationToDetailed(athlete: Athlete) = viewModelScope.launch {
        eventsChannel.send(NavigationEvent.NavigationDetailed(athlete))
    }

    sealed class NavigationEvent {
        object NavigationAdd : NavigationEvent()
        data class NavigationDetailed(val athlete: Athlete) : NavigationEvent()
    }
}
package com.dz.mobile.gympaiement.view.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dz.mobile.gympaiement.data.bo.Athlete
import com.dz.mobile.gympaiement.data.dao.AthleteDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AthleteViewModel @Inject constructor(
    private val athleteDao: AthleteDao
) : ViewModel() {

    private val queryState = MutableStateFlow("")
    private val refreshState = MutableStateFlow(Date())

    val athletes = combine(queryState,refreshState) {
        query,refresh -> Pair(query,refresh)
    }.flatMapLatest { (query, _) ->
        athleteDao.getAthletes(query)
    }

    fun queryChange(query: String) {
        queryState.value = query.replace("'","''")
    }

    fun refresh() {
        refreshState.value = Date()
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
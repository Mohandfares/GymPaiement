package com.dz.mobile.gympaiement.view.ui.list

import androidx.lifecycle.ViewModel
import com.dz.mobile.gympaiement.data.dao.AthleteDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
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
}
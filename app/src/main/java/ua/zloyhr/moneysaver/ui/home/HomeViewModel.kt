package ua.zloyhr.moneysaver.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ua.zloyhr.moneysaver.data.db.SortQueryBy
import ua.zloyhr.moneysaver.data.entities.ChargeItem
import ua.zloyhr.moneysaver.data.repositories.DatabaseRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: DatabaseRepository) : ViewModel() {
    private val sortQueryFlow = MutableStateFlow(SortQueryBy.TIME_CREATED)
    private val isSortDecreasingFlow = MutableStateFlow(true)
    private val queryStringFlow = MutableStateFlow("")

    @ExperimentalCoroutinesApi
    val queryFlow = combine(
        queryStringFlow, sortQueryFlow, isSortDecreasingFlow
    ) { query, sort, isDecreasing ->
        Triple(query, sort, isDecreasing)
    }.flatMapLatest { (query, sort, isDecreasing) ->
        repository.chargeDao.getItemsSorted(query, sort, isDecreasing)
    }

    fun onSortedMenuClick(sortQueryBy: SortQueryBy, isSortDecreasing: Boolean = false) {
        if (sortQueryBy == sortQueryFlow.value) {
            isSortDecreasingFlow.value = !isSortDecreasingFlow.value
        } else {
            sortQueryFlow.value = sortQueryBy
            isSortDecreasingFlow.value = isSortDecreasing
        }
    }

    fun onQueryChanged(query: String) {
        queryStringFlow.value = query
    }
}
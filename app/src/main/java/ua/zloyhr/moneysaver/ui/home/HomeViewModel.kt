package ua.zloyhr.moneysaver.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ua.zloyhr.moneysaver.data.db.ShowQuery
import ua.zloyhr.moneysaver.data.db.SortQueryBy
import ua.zloyhr.moneysaver.data.entities.ChargeItem
import ua.zloyhr.moneysaver.data.repositories.DatabaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DatabaseRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    private val sortQueryFlow = state.getLiveData("sortQuery", SortQueryBy.TIME_CREATED)
    private val isSortDecreasingFlow = state.getLiveData("isSortDecreasing", true)
    val queryStringFlow = state.getLiveData("queryString", "")
    private val showQueryFlow = state.getLiveData("showQuery", ShowQuery.ALL)

    @ExperimentalCoroutinesApi
    val queryFlow = combine(
        queryStringFlow.asFlow(),
        sortQueryFlow.asFlow(),
        showQueryFlow.asFlow(),
        isSortDecreasingFlow.asFlow()
    ) { query, sort, show, isDecreasing ->
        Pair(Pair(query, sort), Pair(show, isDecreasing))
    }.flatMapLatest { (a, b) ->
        repository.chargeDao.getItemsSorted(
            search = a.first,
            sortQueryBy = a.second,
            showQuery = b.first,
            decreasing = b.second
        )
    }

    fun onSortedMenuClick(sortQueryBy: SortQueryBy, isSortDecreasing: Boolean = false) {
        if (sortQueryBy == sortQueryFlow.value) {
            isSortDecreasingFlow.value = !isSortDecreasingFlow.value!!
        } else {
            sortQueryFlow.value = sortQueryBy
            isSortDecreasingFlow.value = isSortDecreasing
        }
    }

    fun onShowFilterClick(showQuery: ShowQuery) {
        showQueryFlow.value =
            if (showQuery == showQueryFlow.value) ShowQuery.ALL
            else showQuery
    }

    fun onSavedInstanceState(state: Bundle) {
        state.putSerializable("sortQuery", sortQueryFlow.value)
        state.putBoolean("isSortDecreasing", isSortDecreasingFlow.value!!)
        state.putString("queryString", queryStringFlow.value)
        state.putSerializable("showQuery", showQueryFlow.value)

    }


    fun onSavePreferences(preferences: SharedPreferences){
        preferences.edit().apply {
            putString("sortQuery", sortQueryFlow.value.toString())
            putBoolean("isSortDecreasing", isSortDecreasingFlow.value!!)
            putString("queryString", queryStringFlow.value)
            putString("showQuery",showQueryFlow.value.toString())
            apply()
        }
    }

    fun onLoadPreferences(preferences: SharedPreferences){
        sortQueryFlow.value = SortQueryBy.valueOf(preferences.getString("sortQuery",null)?:"TIME_CREATED")
        isSortDecreasingFlow.value = preferences.getBoolean("isSortDecreasing",true)
        queryStringFlow.value = preferences.getString("queryString","")
        showQueryFlow.value = ShowQuery.valueOf(preferences.getString("showQuery",null)?:"ALL")
    }


    fun onQueryChanged(query: String) {
        queryStringFlow.value = query
    }

    fun onResetFilters() {
        sortQueryFlow.value = SortQueryBy.TIME_CREATED
        isSortDecreasingFlow.value = true
        queryStringFlow.value = ""
        showQueryFlow.value = ShowQuery.ALL
    }


    fun onDeleteItem(item: ChargeItem){
        viewModelScope.launch {
            repository.chargeDao.delete(item)
        }
    }

    fun onInsertItem(item: ChargeItem){
        viewModelScope.launch {
            repository.chargeDao.insert(item)
        }
    }
}
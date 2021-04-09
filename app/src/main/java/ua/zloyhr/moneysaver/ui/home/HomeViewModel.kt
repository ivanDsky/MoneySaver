package ua.zloyhr.moneysaver.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ua.zloyhr.moneysaver.data.entities.ChargeItem
import ua.zloyhr.moneysaver.data.repositories.DatabaseRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: DatabaseRepository) : ViewModel() {
    fun getItems() = repository.chargeDao.getAllItems()

}
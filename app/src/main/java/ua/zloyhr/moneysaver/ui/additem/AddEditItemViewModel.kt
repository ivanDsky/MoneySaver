package ua.zloyhr.moneysaver.ui.additem

import android.util.Log
import android.util.Log.ASSERT
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ua.zloyhr.moneysaver.data.entities.ChargeItem
import ua.zloyhr.moneysaver.data.repositories.DatabaseRepository
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddEditItemViewModel @Inject constructor(
    private val repository: DatabaseRepository,
    private val state: SavedStateHandle
) :
    ViewModel() {

    var task: ChargeItem? = state.get<ChargeItem>("item")

    fun onSendClick(name: String, value: String, date: String) {
        task = task?.copy(name,value.toDouble(),SimpleDateFormat.getDateInstance().parse(date).time)
            ?: ChargeItem(name,value.toDouble(),SimpleDateFormat.getDateInstance().parse(date).time)

        viewModelScope.launch {
            repository.chargeDao.insert(task!!)
        }
    }
}


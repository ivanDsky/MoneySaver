package ua.zloyhr.moneysaver.ui.graph

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ua.zloyhr.moneysaver.data.TimePeriod
import ua.zloyhr.moneysaver.data.TimeSample
import ua.zloyhr.moneysaver.data.db.ShowQuery
import ua.zloyhr.moneysaver.data.db.SortQueryBy
import ua.zloyhr.moneysaver.data.entities.ChargeItem
import ua.zloyhr.moneysaver.data.repositories.DatabaseRepository
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class GraphViewModel @Inject constructor(private val repository: DatabaseRepository) : ViewModel() {

    val timeSamples: MutableStateFlow<List<TimeSample>> = MutableStateFlow(listOf())
    private val timePeriodFlow = MutableStateFlow(TimePeriod.MONTH)

    fun getItems() = viewModelScope.launch {
        val items = repository.chargeDao.getItemsSorted("", SortQueryBy.TIME_CREATED, ShowQuery.ALL)
        items.combine(timePeriodFlow) { it, timePeriod -> Pair(it, timePeriod) }
            .collect { (it, timePeriod) ->
                val samples = mutableListOf<TimeSample>()
                if (it.isEmpty()) {
                    timeSamples.value = samples
                    return@collect
                }

                var startTime = getStartTime(it.first().timeCreated, timePeriod)

                var currentTimeSample = TimeSample(timePeriod, startTime, 0.0)
                for (item in it) {
                    if (currentTimeSample.endTime <= item.timeCreated) {
                        samples.add(currentTimeSample)
                        startTime = getStartTime(item.timeCreated, timePeriod)
                        currentTimeSample = TimeSample(timePeriod, startTime, 0.0)
                    }
                    currentTimeSample.value += item.value
                }
                samples.add(currentTimeSample)

                timeSamples.value = samples
            }
    }

    fun getPieEntries(samples: List<TimeSample>): List<PieEntry> =
        samples.map { PieEntry(abs(it.value.toFloat()), it.periodName) }

    fun getBarEntries(samples: List<TimeSample>): List<BarEntry> =
        samples.mapIndexed { index, timeSample ->
            BarEntry(
                index.toFloat(),
                timeSample.value.toFloat()
            )
        }

    fun getLabelOfEntries(samples: List<TimeSample>): List<String> =
        samples.map { it.periodName }

    fun onSampleChange(timePeriod: TimePeriod) {
        timePeriodFlow.value = timePeriod
    }

    private fun getStartTime(currentTime: Long, timePeriod: TimePeriod): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTime
        if (timePeriod != TimePeriod.DAY) calendar.set(Calendar.DAY_OF_WEEK, 1) else
            return calendar.timeInMillis
        if (timePeriod != TimePeriod.WEEK) calendar.set(Calendar.DAY_OF_MONTH, 1) else
            return calendar.timeInMillis
        if (timePeriod != TimePeriod.MONTH) calendar.set(Calendar.MONTH, 1)

        return calendar.timeInMillis
    }


    fun onLoadPreferences(preferences: SharedPreferences){
        timePeriodFlow.value = TimePeriod.valueOf(preferences.getString("timePeriod",null)?:"MONTH")
    }

    fun onSavePreferences(preferences: SharedPreferences){
        preferences.edit().putString("timePeriod",timePeriodFlow.value.toString()).apply()
    }
}
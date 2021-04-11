package ua.zloyhr.moneysaver.data

import ua.zloyhr.moneysaver.util.doubleToMoney
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

data class TimeSample(val timePeriod: TimePeriod, val startTime: Long, var value: Double){
    val endTime = timePeriod.getTime(startTime) + startTime
    val periodName:String
        get() {
            val ret = when(timePeriod){
                TimePeriod.DAY -> SimpleDateFormat("dd MMM", Locale.ROOT).format(startTime)
                TimePeriod.WEEK ->
                    SimpleDateFormat("dd MMM", Locale.ROOT).format(startTime) + " - " +
                            SimpleDateFormat("dd MMM", Locale.ROOT).format(endTime - 1)
                TimePeriod.MONTH -> SimpleDateFormat("MMMM",Locale.ROOT).format(startTime)
                TimePeriod.YEAR -> SimpleDateFormat("yyyy",Locale.ROOT).format(startTime)
            }
            return ret.toString()
        }
}

enum class TimePeriod{
    DAY,
    WEEK,
    MONTH,
    YEAR
}

fun TimePeriod.getTime(startTime: Long): Long{
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = startTime
    when(this){
        TimePeriod.DAY -> calendar.add(Calendar.DAY_OF_YEAR,1)
        TimePeriod.WEEK -> calendar.add(Calendar.WEEK_OF_YEAR,1)
        TimePeriod.MONTH -> calendar.add(Calendar.MONTH,1)
        TimePeriod.YEAR -> calendar.add(Calendar.YEAR,1)
    }
    return calendar.timeInMillis - startTime
}
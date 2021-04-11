package ua.zloyhr.moneysaver.util

import java.util.*
import kotlin.math.abs

fun doubleToMoney(value: Double) = (if(value > 0) "+" else if(value < 0) "-" else "") + String.format(Locale.ROOT,"%.2f",abs(value)) + "$"
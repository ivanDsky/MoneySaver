package ua.zloyhr.moneysaver.data.repositories

import ua.zloyhr.moneysaver.data.db.ChargeDao
import javax.inject.Inject

class DatabaseRepository @Inject constructor(val chargeDao: ChargeDao){
}
package ua.zloyhr.moneysaver.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ua.zloyhr.moneysaver.data.entities.ChargeItem

@Database(entities = [ChargeItem::class], exportSchema = false, version = 1)
abstract class ChargeDatabase: RoomDatabase() {
    abstract fun chargeDao() : ChargeDao
}
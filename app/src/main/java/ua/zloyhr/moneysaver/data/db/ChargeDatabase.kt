package ua.zloyhr.moneysaver.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.zloyhr.moneysaver.data.entities.ChargeItem
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [ChargeItem::class], exportSchema = false, version = 1)
abstract class ChargeDatabase : RoomDatabase() {
    abstract fun chargeDao(): ChargeDao

    class Callback @Inject constructor(
        private val database: Provider<ChargeDatabase>,
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().chargeDao()
            val today = Calendar.getInstance().timeInMillis
            val day = 1000 * 60 * 60 * 24

            CoroutineScope(Dispatchers.IO).launch {
                dao.insert(ChargeItem("New TV", -800.0,today - 17 * day))
                dao.insert(ChargeItem("Salary", 3500.0,today - 11 * day))
                dao.insert(ChargeItem("Grocery shopping", -115.0,today - 3 * day))
                dao.insert(ChargeItem("Barbershop", -10.0,today - 1 * day))
            }
        }
    }

}
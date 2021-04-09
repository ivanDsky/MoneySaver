package ua.zloyhr.moneysaver.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ua.zloyhr.moneysaver.data.entities.ChargeItem

@Dao
interface ChargeDao {
    @Query("SELECT * FROM charges_table")
    fun getAllItems() : Flow<List<ChargeItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item : ChargeItem)

    @Update
    suspend fun update(item : ChargeItem)

    @Delete
    suspend fun delete(item : ChargeItem)
}
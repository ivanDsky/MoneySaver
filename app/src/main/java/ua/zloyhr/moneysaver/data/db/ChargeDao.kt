package ua.zloyhr.moneysaver.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ua.zloyhr.moneysaver.data.entities.ChargeItem

@Dao
interface ChargeDao {
    @Query("SELECT * FROM charges_table WHERE name LIKE ('%' || :search || '%') ORDER BY timeCreated DESC")
    fun getItems(search: String): Flow<List<ChargeItem>>

    @Query("SELECT * FROM charges_table WHERE name LIKE ('%' || :search || '%') ORDER BY CASE WHEN :sortBy = 'timeCreated' THEN timeCreated END ASC," +
            "CASE WHEN :sortBy = 'name' THEN name END ASC," +
            "CASE WHEN :sortBy = 'value' THEN value END ASC," +
            "CASE WHEN :sortBy = 'id' THEN id END ASC")
    fun getItemsSortedAsc(search: String, sortBy: String): Flow<List<ChargeItem>>

    @Query("SELECT * FROM charges_table WHERE name LIKE ('%' || :search || '%') ORDER BY CASE WHEN :sortBy = 'timeCreated' THEN timeCreated END DESC," +
            "CASE WHEN :sortBy = 'name' THEN name END DESC," +
            "CASE WHEN :sortBy = 'value' THEN value END DESC," +
            "CASE WHEN :sortBy = 'id' THEN id END DESC")
    fun getItemsSortedDesc(search: String, sortBy: String): Flow<List<ChargeItem>>

    fun getItemsSorted(search: String, sortQueryBy: SortQueryBy, decreasing: Boolean = false) =
        if (decreasing) getItemsSortedDesc(search, sortQueryBy.getSort())
        else getItemsSortedAsc(search, sortQueryBy.getSort())

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ChargeItem)

    @Update
    suspend fun update(item: ChargeItem)

    @Delete
    suspend fun delete(item: ChargeItem)
}

enum class SortQueryBy {
    TIME_CREATED,
    NAME,
    VALUE,
    ID
}

fun SortQueryBy.getSort() = when (this) {SortQueryBy.TIME_CREATED -> "timeCreated"
    SortQueryBy.NAME -> "name"
    SortQueryBy.VALUE -> "value"
    SortQueryBy.ID -> "id"
}

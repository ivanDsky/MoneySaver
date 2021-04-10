package ua.zloyhr.moneysaver.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ua.zloyhr.moneysaver.data.entities.ChargeItem

@Dao
interface ChargeDao {
    @Query("""
        SELECT * FROM charges_table WHERE 
        (CASE WHEN :showBy = 'POSITIVE' THEN value >= 0 
        WHEN :showBy = 'NEGATIVE' THEN value <= 0 
        ELSE 1 END) 
        AND (name LIKE ('%' || :search || '%')) ORDER BY 
        CASE WHEN :sortBy = 'TIME_CREATED' THEN timeCreated
        WHEN :sortBy = 'NAME' THEN name 
        WHEN :sortBy = 'VALUE' THEN value 
        WHEN :sortBy = 'ID' THEN id END ASC
        """)
    fun getItemsSortedAsc(search: String, sortBy: String, showBy: String): Flow<List<ChargeItem>>

    @Query("""
        SELECT * FROM charges_table WHERE 
        (CASE WHEN :showBy = 'POSITIVE' THEN value >= 0 
        WHEN :showBy = 'NEGATIVE' THEN value <= 0 
        ELSE 1 END) 
        AND (name LIKE ('%' || :search || '%')) ORDER BY 
        CASE WHEN :sortBy = 'TIME_CREATED' THEN timeCreated
        WHEN :sortBy = 'NAME' THEN name 
        WHEN :sortBy = 'VALUE' THEN value 
        WHEN :sortBy = 'ID' THEN id END DESC
    """)
    fun getItemsSortedDesc(search: String, sortBy: String, showBy: String): Flow<List<ChargeItem>>

    fun getItemsSorted(search: String, sortQueryBy: SortQueryBy, showQuery: ShowQuery, decreasing: Boolean = false) =
        if (decreasing) getItemsSortedDesc(search, sortQueryBy.toString(), showQuery.toString())
        else getItemsSortedAsc(search, sortQueryBy.toString(), showQuery.toString())

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

enum class ShowQuery{
    ALL,
    POSITIVE,
    NEGATIVE
}

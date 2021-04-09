package ua.zloyhr.moneysaver.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "charges_table")
data class ChargeItem(val name: String,
                      val value: Double,
                      val timeCreated: Long = System.currentTimeMillis(),
                      @PrimaryKey(autoGenerate = true) val id: Int = 0) : Parcelable {
}
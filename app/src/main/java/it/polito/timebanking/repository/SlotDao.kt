package it.polito.timebanking.repository
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SlotDao {
    @Query("SELECT * from slots")
    fun findAll(): LiveData<List<Slot>>

    @Query("SELECT * from slots WHERE id= :id")
    fun searchSlotByID(id: Long?): LiveData<Slot>

    @Insert
    fun addSlot(slot: Slot): Long

    @Query("DELETE FROM slots WHERE id = :id")
    fun removeSlotById(id:String)

    @Query("DELETE FROM slots")
    fun removeAll()
}
package it.polito.timebanking.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.timebanking.repository.Slot
import it.polito.timebanking.repository.TimeSlotRepository
import kotlin.concurrent.thread

class TimeSlotViewModel(application: Application): AndroidViewModel(application) {
    val repo = TimeSlotRepository(application)
    val slots: LiveData<List<Slot>>? = repo.getAllSlots()
    //slots
    fun addSlot(slot: Slot):Long?{
        var id:Long? = 0
        thread {
            id = slot.description?.let {
                repo.addSlot(slot.title,
                    it, slot.date, slot.time, slot.duration, slot.location) //todo??
            }
        }
        return id
    }
    fun updateSlot(slot: Slot){
        thread {
                repo.updateSlot(slot)
        }
    }

    fun getSlotById(id: Long?) = repo.getSlotById(id)

    fun getAllSlot() = repo.getAllSlots()

    fun removeSlot(id:String){
        thread {
            repo.removeSlotById(id)
        }
    }

    fun clearSlots(){
        thread {
            repo.clearAllSlots()
        }
    }
}
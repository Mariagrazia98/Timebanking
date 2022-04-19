package it.polito.timebanking.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import it.polito.timebanking.repository.Slot
import it.polito.timebanking.repository.TimeSlotRepository
import it.polito.timebanking.repository.User
import it.polito.timebanking.repository.UserRepository
import kotlin.concurrent.thread

class TimeSlotViewModel(application: Application): AndroidViewModel(application) {
    val repo = TimeSlotRepository(application)
    val slots: LiveData<List<Slot>>? = repo.getAllSlots()
    //slots
    fun addSlot(title:String,description:String,date:String,time:String,duration: Int, location: String):Long?{
        var id:Long? = 0
        thread {
            id = repo.addSlot(title, description, date, time, duration, location)
        }
        return id
    }

    fun getSlotById(id:String) = repo.getSlotById(id)

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
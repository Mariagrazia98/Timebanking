package it.polito.timebanking.repository

import android.app.Application
import androidx.lifecycle.LiveData

class TimeSlotRepository(application: Application) {

    private val slotDao = SlotDatabase.getDatabase(application)?.slotDao()

    //slots
    fun addSlot(title:String,description:String,date:String,time:String,duration: Int, location: String):Long?{
        val s = Slot().also{ it.title=title; it.description=description; it.date=date; it.time=time; it.duration=duration; it.location=location}
        val id_returned:Long? = slotDao?.addSlot(s)
        return id_returned
    }

    fun clearAllSlots(){
        slotDao?.removeAll()
    }

    fun removeSlotById(id: String){
        slotDao?.removeSlotById(id)
    }

    fun getAllSlots(): LiveData<List<Slot>>? = slotDao?.findAll()

    fun getSlotById(id: Long?): LiveData<Slot>? = slotDao?.searchSlotByID(id)
}
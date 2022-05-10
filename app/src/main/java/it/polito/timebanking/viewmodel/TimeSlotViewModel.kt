package it.polito.timebanking.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.polito.timebanking.model.TimeSlotFire
import it.polito.timebanking.model.UserFire
import it.polito.timebanking.repository.Slot
import it.polito.timebanking.repository.TimeSlotRepository
import it.polito.timebanking.repository.UserRepository
import kotlinx.coroutines.launch
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



    //firebase
    private lateinit var slot: TimeSlotFire

    fun getSlot(): TimeSlotFire {
        return slot
    }

    fun setSlot(slot: TimeSlotFire) {
        this.slot = slot
    }

    fun getNewSlotId(userId :String) : String{
        return repo.getNewSlotId(userId)
    }

    fun updateSlotF(userId: String, slot: TimeSlotFire) : LiveData<Boolean>{
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch{
            val result = repo.updateSlotF(userId, slot)
            res.postValue(result)
        }
        return res
    }

    fun removeSlotF(userId: String, slotId: String) : LiveData<Boolean>{
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch{
            val result = repo.removeSlotF(userId, slotId)
            res.postValue(result)
        }
        return res
    }

    fun getSlotFById(userId: String, slotId: String) : LiveData<TimeSlotFire?> {
        val res = MutableLiveData<TimeSlotFire?>()
        viewModelScope.launch{
            val result = repo.getSlotFById(userId, slotId)
            res.postValue(result.getOrNull())
        }
        return res
    }

    fun getAllSlotsByUser(userId: String) : LiveData<List<TimeSlotFire>>{
        val res = MutableLiveData<List<TimeSlotFire>>()
        viewModelScope.launch{
            val result = repo.getAllSlotsByUser(userId)
            res.postValue(result.getOrNull())
        }
        return res
    }

    fun getAllSkills() : LiveData<List<String>>{
        val res = MutableLiveData<List<String>>()
        viewModelScope.launch{
            val result = repo.getAllSkills()
            res.postValue(result.getOrNull())
        }
        return res
    }

    fun getSlotsBySkill(skill: String) : LiveData<List<TimeSlotFire>>{
        val res = MutableLiveData<List<TimeSlotFire>>()
        viewModelScope.launch{
            val result = repo.getSlotsBySkill(skill)
            res.postValue(result.getOrNull())
        }
        return res
    }
}

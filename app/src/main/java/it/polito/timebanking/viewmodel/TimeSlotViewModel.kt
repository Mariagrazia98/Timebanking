package it.polito.timebanking.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.polito.timebanking.model.TimeSlotFire
import it.polito.timebanking.model.UserFire
import it.polito.timebanking.repository.Slot
import it.polito.timebanking.repository.TimeSlotRepository
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class TimeSlotViewModel(application: Application): AndroidViewModel(application) {
    val repo = TimeSlotRepository(application)
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

    fun updateSlot(userId: String, slot: TimeSlotFire) : LiveData<Boolean>{
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch{
            val result = repo.updateSlotF(userId, slot)
            res.postValue(result)
        }
        return res
    }

    fun removeSlot(userId: String, slotId: String) : LiveData<Boolean>{
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

    fun getSlotsByUser(userId: String) :LiveData<Map<UserFire, List<TimeSlotFire>>>{
        val res = MutableLiveData<Map<UserFire, List<TimeSlotFire>>>()
        viewModelScope.launch{
            val result = repo.getSlotsByUser(userId)
            res.postValue(result.getOrNull())
        }
        return res
    }

    fun getAllSkills(userId: String) : LiveData<List<String>>{
        val res = MutableLiveData<List<String>>()
        viewModelScope.launch{
            val result = repo.getAllSkills(userId)
            res.postValue(result.getOrNull())
        }
        return res
    }

    fun getSlotsBySkill(userId:String, skill: String) : LiveData<Map<UserFire, List<TimeSlotFire>>>{
        val res = MutableLiveData<Map<UserFire, List<TimeSlotFire>>>()
        viewModelScope.launch{
            val result = repo.getSlotsBySkill(userId, skill)
            res.postValue(result.getOrNull())
        }
        return res
    }
}

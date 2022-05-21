package it.polito.timebanking.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.polito.timebanking.model.ChatMessage
import it.polito.timebanking.model.TimeSlot
import it.polito.timebanking.model.User
import it.polito.timebanking.repository.TimeSlotRepository
import kotlinx.coroutines.launch

class TimeSlotViewModel(application: Application): AndroidViewModel(application) {
    val repo = TimeSlotRepository()
    private lateinit var slot: TimeSlot

    fun getSlot(): TimeSlot {
        return slot
    }

    fun setSlot(slot: TimeSlot) {
        this.slot = slot
    }

    fun getNewSlotId(userId :String) : String{
        return repo.getNewSlotId(userId)
    }

    fun updateSlot(userId: String, slot: TimeSlot) : LiveData<Boolean>{
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch{
            val result = repo.updateSlot(userId, slot)
            res.postValue(result)
        }
        return res
    }

    fun getSlotFById(userId: String, slotId: String) : LiveData<TimeSlot?> {
        val res = MutableLiveData<TimeSlot?>()
        viewModelScope.launch{
            val result = repo.getSlotFById(userId, slotId)
            res.postValue(result.getOrNull())
        }
        return res
    }

    fun getSlotsByUser(userId: String) :LiveData<Map<User, List<TimeSlot>>>{
        val res = MutableLiveData<Map<User, List<TimeSlot>>>()
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

    fun getSlotsBySkill(userId:String, skill: String) : LiveData<Map<User, List<TimeSlot>>>{
        val res = MutableLiveData<Map<User, List<TimeSlot>>>()
        viewModelScope.launch{
            val result = repo.getSlotsBySkill(userId, skill)
            res.postValue(result.getOrNull())
        }
        return res
    }

    fun removeSlot(userId: String, slotId: String) : LiveData<Boolean>{
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch{
            val result = repo.removeSlot(userId, slotId)
            res.postValue(result)
        }
        return res
    }


    fun getSlotChatWithOfferer(uidCurrent: String, uidOfferer: String, slotId: String) : LiveData<List<ChatMessage>?> {
        val res = MutableLiveData<List<ChatMessage>?>()
        viewModelScope.launch{
            val result = repo.getSlotChatWithOfferer(uidCurrent, uidOfferer, slotId)
            res.postValue(result.getOrNull())
        }
        return res
    }

    /*
    fun getSlotChatWithAsker(uidCurrent: String, uidOfferer: String, slotId: String) : LiveData<List<Message>?> {
        val res = MutableLiveData<TimeSlot?>()
        viewModelScope.launch{
            val result = repo.getSlotChatWithOfferer(uidCurrent, uidOfferer, slotId)
            res.postValue(result.getOrNull())
        }
        return res
    }
    */
}

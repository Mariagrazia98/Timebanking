package it.polito.timebanking.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import it.polito.timebanking.model.*
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
        Log.d("VIEW MODEL", "make the query")
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch{
            val result = repo.updateSlot(userId, slot)
            res.postValue(result)
        }
        return res
    }

    /*
    fun getSlotFById(userId: String, slotId: String) : LiveData<TimeSlot?> {
        val res = MutableLiveData<TimeSlot?>()
        viewModelScope.launch{
            val result = repo.getSlotFById(userId, slotId)
            res.postValue(result.getOrNull())
        }
        return res
    }
     */

    fun getSlotFById(userId: String, slotId: String) : MutableLiveData<TimeSlot> {
        var ts = MutableLiveData<TimeSlot>()

        repo.getSlotFById(userId, slotId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("Error", "Error listen failed")
            }
            if (snapshot != null && snapshot.exists()) {
                ts.postValue(snapshot.toObject(TimeSlot::class.java))
            } else {
                Log.d("Error", "Current data null")
            }
        }

        return ts
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

    fun getInterestedSlotsByUser(userId: String) :LiveData<Map<User, List<TimeSlot>>>{
        val res = MutableLiveData<Map<User, List<TimeSlot>>>()
        viewModelScope.launch{
            val result = repo.getInterestedSlotsByUser(userId)
            res.postValue(result.getOrNull())
        }
        return res
    }


    /*** CHAT ***/
    fun getChat(idAsker :String, slotId: String, idOfferer: String) : LiveData<Chat?>{
        val res = MutableLiveData<Chat?>()
        viewModelScope.launch{
            val result = repo.getChat(idAsker, slotId, idOfferer)
            res.postValue(result.getOrNull())
        }
        return res
    }

    fun getNewChatId(idOfferer :String, slotId: String) : String{
        return repo.getNewChatId(idOfferer, slotId)
    }

    fun addChat(idOfferer: String, slotId: String, chatId: String, chat: Chat): LiveData<Boolean>{
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val result = repo.addChat(idOfferer, slotId, chatId, chat)
            res.postValue(result)
        }
        return res
    }

    fun getNewChatMessageId(idOfferer: String, slotId: String, chatId: String) : String{
        return repo.getNewChatMessageId(idOfferer, slotId, chatId)
    }

    fun addChatMessage(idOfferer: String, slotId: String, chatId: String, msg: ChatMessage): LiveData<Boolean>{
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val result = repo.addChatMessage(idOfferer, slotId, chatId, msg)
            res.postValue(result)
        }
        return res
    }

    //retrieve the chat messages exchanged between the offerer of a specific timeslot and the current user who is asking the timeslot
    fun getSlotChatMessages(idOfferer: String, slotId: String, chatId: String) : MutableLiveData<MutableList<ChatMessage>?> {
        val res = MutableLiveData<MutableList<ChatMessage>?>()
        viewModelScope.launch{
            val result = repo.getSlotChatMessages(idOfferer, slotId, chatId)
            res.postValue(result.getOrNull())
        }
        return res
    }

    //retrieve all started chats (incoming requests by other user to the offerer -> current user) for a specific timeslot
    fun getChatsSlotIncomingRequests(idOfferer: String, slotId: String) : LiveData<List<ChatUser>?> {
        val res = MutableLiveData<List<ChatUser>?>()
        viewModelScope.launch{
            val result = repo.getChatsSlotIncomingRequests(idOfferer, slotId)
            res.postValue(result.getOrNull())
        }
        return res
    }

    fun rejectChat(idOfferer: String, timeslotId: String, chatId: String): LiveData<Boolean> {
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch{
            val result = repo.updateChatStatus(idOfferer, timeslotId, chatId)
            res.postValue(result)
        }
        return res
    }

}

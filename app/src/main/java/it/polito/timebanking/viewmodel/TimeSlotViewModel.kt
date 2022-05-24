package it.polito.timebanking.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.polito.timebanking.model.Chat
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






    fun getChatId(userId :String, slotId: String, uidOfferer: String) : LiveData<String?>{
        val res = MutableLiveData<String?>()
        viewModelScope.launch{
            val result = repo.getChatId(userId, slotId, uidOfferer)
            res.postValue(result)
        }
        return res
    }

    fun getNewChatId(userIdOfferer :String, slotId: String) : String{
        return repo.getNewChatId(userIdOfferer, slotId)
    }

    fun addChat(userIdOfferer: String, slotId: String, chatId: String, chat: Chat): LiveData<Boolean>{
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val result = repo.addChat(userIdOfferer, slotId, chatId, chat)
            res.postValue(result)
        }
        return res
    }

    fun getNewChatMessageId(userId: String, slotId: String, chatId: String) : String{
        return repo.getNewChatMessageId(userId, slotId, chatId)
    }

    fun addChatMessage(userId: String, slotId: String, chatId: String, msg: ChatMessage): LiveData<Boolean>{
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val result = repo.addChatMessage(userId, slotId, chatId, msg)
            res.postValue(result)
        }
        return res
    }

    //retrieve the chat messages exchanged between the offerer of a specific timeslot and the current user who is asking the timeslot
    fun getSlotChatWithOfferer(uidCurrent: String, uidOfferer: String, slotId: String, chatId: String) : MutableLiveData<MutableList<ChatMessage>?> {
        val res = MutableLiveData<MutableList<ChatMessage>?>()
        viewModelScope.launch{
            val result = repo.getSlotChatWithOfferer(uidCurrent, uidOfferer, slotId, chatId)
            res.postValue(result.getOrNull())
        }
        return res
    }

    //retrieve all started chats (incoming requests by other user to the offerer -> current user) for a specific timeslot
    fun getChatsSlotIncomingRequests(uidCurrent: String, slotId: String) : LiveData<List<Chat>?> {
        val res = MutableLiveData<List<Chat>?>()
        viewModelScope.launch{
            val result = repo.getChatsSlotIncomingRequests(uidCurrent, slotId)
            res.postValue(result.getOrNull())
        }
        return res
    }

    //retrieve the chat messages exchanged between the current user (offerer) and another user who is asking for the timeslot
    fun getSlotChatWithAsker(uidCurrent: String, slotId: String, chat: Chat) : LiveData<List<ChatMessage>?> {
        val res = MutableLiveData<List<ChatMessage>?>()
        viewModelScope.launch{
            val result = repo.getSlotChatWithAsker(uidCurrent, slotId, chat)
            res.postValue(result.getOrNull())
        }
        return res
    }

}

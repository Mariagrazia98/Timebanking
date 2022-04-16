package it.polito.timebanking.repository

import android.app.Application
import androidx.lifecycle.LiveData
import java.time.Duration

class Repository(application: Application) {
    private val userDao = UserDatabase.getDatabase(application)?.userDao()
    private val slotDao = SlotDatabase.getDatabase(application)?.slotDao()

    //users
    fun addUser(fullname:String,nickname:String,email:String,location:String){
        val u = User().also{ it.fullname=fullname; it.email=email; it.location=location; it.nickname=nickname}
        userDao?.addUser(u)
    }

    fun clearAllUSers(){
        userDao?.removeAll()
    }

    fun removeUserById(id: String){
        userDao?.removeUserById(id)
    }

    fun getAllUsers(): LiveData<List<User>>? = userDao?.findAll()

    fun getUserById(id:String): LiveData<User>? = userDao?.searchUserByID(id)

    //slots
    fun addSlot(title:String,description:String,date:String,time:String,duration: Int, location: String){
        val s = Slot().also{ it.title=title; it.description=description; it.date=date; it.time=time; it.duration=duration; it.location=location}
        slotDao?.addSlot(s)
    }

    fun clearAllSlots(){
        slotDao?.removeAll()
    }

    fun removeSlotById(id: String){
        slotDao?.removeSlotById(id)
    }

    fun getAllSlots(): LiveData<List<Slot>>? = slotDao?.findAll()

    fun getSlotById(id:String): LiveData<Slot>? = slotDao?.searchSlotByID(id)

}
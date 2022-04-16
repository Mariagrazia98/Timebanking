package it.polito.timebanking.repository

import android.app.Application
import androidx.lifecycle.LiveData
import java.time.Duration

class Repository(application: Application) {
    private val userDao = UserDatabase.getDatabase(application)?.userDao()
    private val slotDao = SlotDatabase.getDatabase(application)?.slotDao()

    //users
    fun addUser(fullname:String,nickname:String,email:String,location:String): Long? {
        val u = User().also{ it.fullname=fullname; it.email=email; it.location=location; it.nickname=nickname}
        val id_returned: Long? = userDao?.addUser(u)
        return id_returned
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

    fun getSlotById(id:String): LiveData<Slot>? = slotDao?.searchSlotByID(id)

}
package it.polito.timebanking.repository

import android.app.Application

class Repository(application: Application) {
    private val userDao = UserDatabase.getDatabase(application)?.userDao()
    private val slotDao = SlotDatabase.getDatabase(application)?.slotDao()

    fun addUser(fullname:String,nickname:String,email:String,location:String){
        val u = User().also{ it.fullname=fullname; it.email=email; it.location=location; it.nickname=nickname}
        userDao?.addUser(u)
    }
}
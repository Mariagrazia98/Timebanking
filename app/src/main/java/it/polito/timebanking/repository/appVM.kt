package it.polito.timebanking.repository

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kotlin.concurrent.thread

class appVM(application: Application): AndroidViewModel(application) {
    val repo = Repository(application)
    val users: LiveData<List<User>>? = repo.getAllUsers()
    val slots: LiveData<List<Slot>>? = repo.getAllSlots()

    //users
    fun addUser(fullname:String,nickname:String,email:String,location:String):Long? {
        var id:Long? = 0
        thread {
            id = repo.addUser(fullname,nickname,email,location)
        }
        return id
    }

    fun getUserById(id:String) = repo.getUserById(id)

    fun removeUser(id:String){
        thread {
            repo.removeUserById(id)
        }
    }

    fun clearUsers(){
        thread {
            repo.clearAllUSers()
        }
    }
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
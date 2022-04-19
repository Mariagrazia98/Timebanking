package it.polito.timebanking.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import it.polito.timebanking.repository.User
import it.polito.timebanking.repository.UserRepository
import kotlin.concurrent.thread

class ProfileViewModel(application: Application): AndroidViewModel(application)  {
    val repo = UserRepository(application)
    val users: LiveData<List<User>>? = repo.getAllUsers()

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
            repo.clearAllUsers()
        }
    }
}
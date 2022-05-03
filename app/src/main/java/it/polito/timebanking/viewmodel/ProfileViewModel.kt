package it.polito.timebanking.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import it.polito.timebanking.repository.User
import it.polito.timebanking.repository.UserRepository
import kotlin.concurrent.thread

class ProfileViewModel(application: Application): AndroidViewModel(application)  {
    val repo = UserRepository(application)


    fun addUser(user: User){
        thread {
            repo.addUser(user)
        }
    }

    fun getAllUsers(): LiveData<List<User>>? = repo.getAllUsers()

    fun updateUser(user: User){
        thread {
            repo.updateUser(user)
        }
    }

    fun getUserById(id:Long) = repo.getUserById(id)

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
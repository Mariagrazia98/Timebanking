package it.polito.timebanking.repository

import android.app.Application
import androidx.lifecycle.LiveData

class UserRepository(application: Application) {
    private val userDao = UserDatabase.getDatabase(application)?.userDao()


    fun addUser(user: User){
        userDao?.addUser(user)
    }

    fun updateUser(user: User) {
        userDao?.updateUser(user)
    }

    fun clearAllUsers(){
        userDao?.removeAll()
    }

    fun removeUserById(id: String){
        userDao?.removeUserById(id)
    }

    fun getAllUsers(): LiveData<List<User>>? = userDao?.findAll()

    fun getUserById(id:Long): LiveData<User>? = userDao?.searchUserByID(id)
}
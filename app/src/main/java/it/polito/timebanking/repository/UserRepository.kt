package it.polito.timebanking.repository

import android.app.Application
import androidx.lifecycle.LiveData

class UserRepository(application: Application) {
    private val userDao = UserDatabase.getDatabase(application)?.userDao()


    fun addUser(fullname:String,nickname:String,email:String,location:String): Long? {
        val u = User().also{ it.fullname=fullname; it.email=email; it.location=location; it.nickname=nickname}
        val id_returned: Long? = userDao?.addUser(u)
        return id_returned
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

    fun getUserById(id:Int): LiveData<User>? = userDao?.searchUserByID(id)
}
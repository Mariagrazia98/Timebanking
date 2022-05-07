package it.polito.timebanking.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentReference
import it.polito.timebanking.model.UserFire
import it.polito.timebanking.repository.User
import it.polito.timebanking.repository.UserRepository
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

class ProfileViewModel(application: Application): AndroidViewModel(application)  {
    private lateinit var user: UserFire

    fun getUser(): UserFire {
        return user
    }

    fun setUser(user: UserFire) {
        this.user = user
    }

    val repo = UserRepository(application)


    fun addUser(user: User){
        thread {
            repo.addUser(user)
        }
    }


    fun getAllUsers(): LiveData<List<User>>? = repo.getAllUsers() /* TODO implement it*/

    fun updateUser(user: User){
        thread {
            repo.updateUser(user)
        }
    }

    fun getUserById(id:Long) = repo.getUserById(id)



    fun clearUsers(){     /*TODO implement it in firebase version*/

        thread {
            repo.clearAllUsers()
        }
    }


    //Firebase
    fun addUserF(user: UserFire): LiveData<Boolean>{
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val result = repo.addUserF(user)
            res.postValue(result)
        }
        return res
    }

    fun getUserByIdF(id:String): LiveData<UserFire?> {
        val res = MutableLiveData<UserFire?>()
        viewModelScope.launch {
            val result = repo.getUserByIdF(id)
            res.postValue(result.getOrNull())
        }
        return res
    }

    fun loginUser(uid: String, updates: HashMap<String, Any>): LiveData<Boolean> {
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val result = repo.loginUser(uid, updates)
            res.postValue(result)
        }
        return res
    }
    fun updateUserF(user: UserFire) : LiveData<Boolean>{
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val result = repo.updateUserF(user)
            res.postValue(result)
        }
        return res

    }



}
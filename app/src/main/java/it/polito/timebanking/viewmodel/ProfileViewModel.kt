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

    //Firebase
    fun addUser(user: UserFire): LiveData<Boolean>{
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val result = repo.addUserF(user)
            res.postValue(result)
        }
        return res
    }

    fun getUserById(id:String): LiveData<UserFire?> {
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
    fun updateUser(user: UserFire) : LiveData<Boolean>{
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val result = repo.updateUserF(user)
            res.postValue(result)
        }
        return res

    }



}
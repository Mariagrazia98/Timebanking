package it.polito.timebanking.viewmodel

import android.app.Application
import androidx.lifecycle.*
import it.polito.timebanking.model.User
import it.polito.timebanking.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application): AndroidViewModel(application)  {
    private lateinit var user: User
    val repo = UserRepository()

    fun getUser(): User {
        return user
    }

    fun setUser(user: User) {
        this.user = user
    }

    fun addUser(user: User): LiveData<Boolean>{
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val result = repo.addUser(user)
            res.postValue(result)
        }
        return res
    }

    fun getUserById(id:String): LiveData<User?> {
        val res = MutableLiveData<User?>()
        viewModelScope.launch {
            val result = repo.getUserById(id)
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
    fun updateUser(user: User) : LiveData<Boolean>{
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val result = repo.updateUser(user)
            res.postValue(result)
        }
        return res

    }

    fun updateUserCredit(uid: String, newCredit: Int) : LiveData<Boolean> {
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val result = repo.updateCreditUser(uid, newCredit)
            res.postValue(result)
        }
        return res
    }
}
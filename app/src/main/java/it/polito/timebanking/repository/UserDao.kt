package it.polito.timebanking.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * from users")
    fun findAll(): LiveData<List<User>>

    @Query("SELECT * from users WHERE id= :id")
    fun searchUserByID(id:String): LiveData<User>

    @Insert
    fun addUser(user: User)

    @Query("DELETE FROM users WHERE id = :id")
    fun removeUserById(id:String)

    @Query("DELETE FROM users")
    fun removeAll()
}
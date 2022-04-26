package it.polito.timebanking.repository

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * from users")
    fun findAll(): LiveData<List<User>>

    @Query("SELECT * from users WHERE id= :id")
    fun searchUserByID(id:Long): LiveData<User>

    //If you insert one entity at a time, you can get its ID back as a Long
    @Insert
    fun addUser(user: User): Long

    @Update
    fun updateUser(user: User)

    @Query("DELETE FROM users WHERE id = :id")
    fun removeUserById(id:String)

    @Query("DELETE FROM users")
    fun removeAll()
}
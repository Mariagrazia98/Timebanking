package it.polito.timebanking.repository

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class UserRepository(application: Application) {
    private val userDao = UserDatabase.getDatabase(application)?.userDao()
    private val db = FirebaseFirestore.getInstance()

    fun addUser(user: User){
        userDao?.addUser(user)
        // Create a new user with a first and last name
        // Create a new user with a first and last name
        Log.d("addd user", "ADD")
        val user: MutableMap<String, Any> = HashMap()
        user["first"] = "Ada"
        user["last"] = "Lovelace"
        user["born"] = 1815
        Log.d("addd user", db.toString())
        // Add a new document with a generated ID

        // Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(
                    TAG,
                    "DocumentSnapshot added with ID: " + documentReference.id
                )
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }
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
package it.polito.timebanking.repository

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.timebanking.model.UserFire
import kotlinx.coroutines.tasks.await


class UserRepository(application: Application) {
    private val userDao = UserDatabase.getDatabase(application)?.userDao()
    private val db = FirebaseFirestore.getInstance()

    fun addUser(user: User) {
        userDao?.addUser(user)
        /*  // Create a new user with a first and last name
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
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }*/
    }

    fun getAllUsers(): LiveData<List<User>>? = userDao?.findAll()
    /*TODO implement it in firebase version*/

    fun updateUser(user: User) {
        userDao?.updateUser(user)
    }

    fun clearAllUsers() {
        userDao?.removeAll()
    }
    /*TODO implement it in firebase version*/


    fun getUserById(id: Long): LiveData<User>? = userDao?.searchUserByID(id)


    //Firebase
    suspend fun addUserF(user: UserFire): Boolean {
        return try {
            db.collection("users")
                .document(user.uid)
                .set(user, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserByIdF(uid: String): Result<UserFire?> {
        return try {
            val data = Firebase.firestore
                .collection("users")
                .document(uid)
                .get()
                   .await()
            Log.d("DATA", data.data.toString())
            Log.d("DATA", data.toObject(UserFire::class.java).toString() )
            Result.success(data.toObject(UserFire::class.java))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(uid: String, updates: HashMap<String,Any>): Boolean {
        return try{
            Firebase.firestore
                .collection("users")
                .document(uid)
                .update(updates)
                .await()
            true
        }catch (e: Exception){
            false
        }
    }

    suspend fun updateUserF( user: UserFire) : Boolean
    {
        return try{
            Firebase.firestore
                .collection("users")
                .document(user.uid)
                .set(user)
                .await()
            true
        }catch (e: Exception){
            false
        }
    }


}
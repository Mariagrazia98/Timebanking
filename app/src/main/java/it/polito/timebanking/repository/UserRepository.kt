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
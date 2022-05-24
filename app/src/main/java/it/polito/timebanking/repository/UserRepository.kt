package it.polito.timebanking.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.timebanking.model.User
import kotlinx.coroutines.tasks.await


class UserRepository() {
    private val db = FirebaseFirestore.getInstance()


    suspend fun addUser(user: User): Boolean {
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

    suspend fun getUserById(uid: String): Result<User?> {
        return try {
            val data = Firebase.firestore
                .collection("users")
                .document(uid)
                .get()
                   .await()
          Result.success(data.toObject(User::class.java))
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

    suspend fun updateUser(user: User) : Boolean
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
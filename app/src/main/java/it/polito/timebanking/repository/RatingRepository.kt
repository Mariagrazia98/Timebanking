package it.polito.timebanking.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.timebanking.model.Review
import it.polito.timebanking.model.TimeSlot
import it.polito.timebanking.model.User
import kotlinx.coroutines.tasks.await

class RatingRepository {
    private val db = FirebaseFirestore.getInstance()

    //add
    fun getNewReviewId(userId: String): String {
        return try {
            val data = Firebase.firestore
                .collection("users")
                .document(userId)
                .collection("reviews")
                .document()
                .id
            data
        } catch (e: Exception) {
            e.toString()
        }
    }

    suspend fun updateReview(userId: String, review: Review): Boolean {
        return try {
            Firebase.firestore
                .collection("users")
                .document(userId)
                .collection("reviews")
                .document(review.id)
                .set(review)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
    //fine add

    suspend fun getReviewsByUserID(uid: String): Result<MutableList<Review>> {
        return try {
            val reviews = Firebase.firestore
                .collection("users")
                .document(uid)
                .collection("reviews")
                .get()
                .await()

            val reviewsUser = mutableListOf<Review>()

            reviews.documents.map {
                if(it.data != null){
                    it.toObject(Review::class.java)?.let { it1 -> reviewsUser.add(it1) }
                }
            }
            Result.success(reviewsUser)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
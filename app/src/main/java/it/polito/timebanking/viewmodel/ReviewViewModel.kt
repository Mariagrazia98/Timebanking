package it.polito.timebanking.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.polito.timebanking.model.Review
import it.polito.timebanking.model.TimeSlot
import it.polito.timebanking.model.User
import it.polito.timebanking.repository.RatingRepository
import kotlinx.coroutines.launch

class ReviewViewModel(application: Application): AndroidViewModel(application) {
    val repo = RatingRepository()

    fun getNewReviewId(userId: String): String {
        return repo.getNewReviewId(userId)
    }

    fun updateReview(userId: String, review: Review): LiveData<Boolean> {
        val res = MutableLiveData<Boolean>()
        viewModelScope.launch {
            val result = repo.updateReview(userId, review)
            res.postValue(result)
        }
        return res
    }

    fun getReviewsByUser(userId: String) : LiveData<MutableList<Review>>{
        val res = MutableLiveData<MutableList<Review>>()
        viewModelScope.launch{
            val result = repo.getReviewsByUserID(userId)
            res.postValue(result.getOrNull())
        }
        return res
    }
}
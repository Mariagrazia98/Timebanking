package it.polito.timebanking

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import it.polito.timebanking.model.Review
import it.polito.timebanking.viewmodel.ProfileViewModel
import it.polito.timebanking.viewmodel.ReviewViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditReviewFragment : Fragment(R.layout.fragment_edit_review) {
    lateinit var ratingBar: RatingBar
    lateinit var comment: EditText
    lateinit var sendReviewBtn: Button
    lateinit var reviewsVM: ReviewViewModel
    lateinit var userId: String
    lateinit var userIdReviewer: String
    lateinit var profileVM: ProfileViewModel
    lateinit var nameReviewer: String

    override fun onPrepareOptionsMenu(menu: Menu) {
        //delete edit_button
        menu.findItem(R.id.edit_button).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).supportActionBar?.title = "Edit Review"
        reviewsVM = ViewModelProvider(requireActivity()).get(ReviewViewModel::class.java)
        profileVM = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)

        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        comment = view.findViewById(R.id.commentReview)
        sendReviewBtn = view.findViewById(R.id.sendReviewBtn)
        ratingBar = view.findViewById(R.id.ratingEditBar)
        sendReviewBtn.setOnClickListener {
            sendReview()
        }

        userId = arguments?.getString("userId")  ?: ""
        userIdReviewer = arguments?.getString(userIdReviewer) ?: ""

        profileVM.getUserById(userIdReviewer).observe(viewLifecycleOwner){
            nameReviewer = it?.nickname ?: ""
        }
    }

    private fun sendReview() {
        val id = reviewsVM.getNewReviewId(userId)
        val review = Review()
        review.comment = comment.text.toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            review.date = LocalDateTime.now().format(formatter).toString()
        }
        review.rating = ratingBar.rating
        review.id = id
        review.nameReviewer = nameReviewer
        review.type = 0
        reviewsVM.updateReview(userId, review).observe(viewLifecycleOwner) {
            Log.d("debugReview", it.toString())
        }
    }
}
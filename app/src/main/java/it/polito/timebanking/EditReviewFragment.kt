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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import it.polito.timebanking.model.Review
import it.polito.timebanking.model.TimeSlot
import it.polito.timebanking.viewmodel.ProfileViewModel
import it.polito.timebanking.viewmodel.ReviewViewModel
import it.polito.timebanking.viewmodel.TimeSlotViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditReviewFragment : Fragment(R.layout.fragment_edit_review) {
    private lateinit var ratingBar: RatingBar
    private lateinit var comment: EditText
    private lateinit var sendReviewBtn: Button
    private lateinit var reviewsVM: ReviewViewModel
    private lateinit var timeSlotVM: TimeSlotViewModel
    private lateinit var userIdReviewer: String
    private lateinit var profileVM: ProfileViewModel
    private lateinit var nameReviewer: String
    private lateinit var tsId: String
    lateinit var userId: String
    var oldReviewState = -1
    lateinit var idReceiver : String

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
        timeSlotVM = ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)

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

        userId = arguments?.getString("userId")!!
        userIdReviewer = arguments?.getString("userIdReviewer")!!
        tsId = arguments?.getString("timeslotId")!!

        profileVM.getUserById(userIdReviewer).observe(viewLifecycleOwner){
            nameReviewer = it?.nickname ?: ""
        }
        timeSlotVM.getSlotFById(userId,tsId).observe(viewLifecycleOwner){
            oldReviewState = it.reviewState
            idReceiver = it.idReceiver!!
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
        review.timeSlotId = tsId
        review.userIdReviewer = userIdReviewer
        //dobbiamo capire il ruolo di chi sta avendo la review (userId) -> type
        //e da chi sta arrivando la review -> role
        var roleReviewer = ""
        if(userId == idReceiver) {
            //quello che riceve la review è chi ha usufruito
            review.type = 1
            roleReviewer = "Offerer"
        }
        else{
            review.type = 0
            roleReviewer = "Receiver"

        }
        timeSlotVM.updateReviewState(userId,tsId,roleReviewer,oldReviewState)
        reviewsVM.updateReview(userId, review)
        Toast.makeText(context, "Review completed!", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }
}
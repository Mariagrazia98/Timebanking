package it.polito.timebanking.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.timebanking.MainActivity
import it.polito.timebanking.R
import it.polito.timebanking.model.Review
import it.polito.timebanking.viewmodel.ReviewViewModel

/**
 * A fragment representing a list of Items.
 */
class ReviewListFragment : Fragment() {
    private lateinit var reviewsVM: ReviewViewModel
    private lateinit var reviewList: LiveData<MutableList<Review>>
    var userId: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reviews_list, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.reviewsRv)
        userId = (activity as MainActivity).reviewsListOf
        reviewsVM = ViewModelProvider(requireActivity()).get(ReviewViewModel::class.java)
        reviewList = reviewsVM.getReviewsByUser(userId!!)
        reviewList.observe(viewLifecycleOwner){
            rv.layoutManager = LinearLayoutManager(context)
            val adapter = reviewList.value?.let { it1 -> ReviewAdapter(it1.toList()) }
            rv.adapter = adapter
        }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).supportActionBar?.title = "Reviews List"
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.edit_button).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }
}
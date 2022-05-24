package it.polito.timebanking

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.timebanking.model.Review
import it.polito.timebanking.viewmodel.ReviewViewModel
import it.polito.timebanking.viewmodel.TimeSlotViewModel

/**
 * A fragment representing a list of Items.
 */
class ReviewListFragment : Fragment() {

    lateinit var reviewsVM: ReviewViewModel
    lateinit var reviewList: LiveData<MutableList<Review>>
    var userId: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reviews_list, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.reviewsRv)
        userId = arguments?.getString("userId")

        reviewsVM = ViewModelProvider(requireActivity()).get(ReviewViewModel::class.java)
        reviewList = reviewsVM.getReviewsByUser(userId!!)
        reviewList.observe(viewLifecycleOwner){
            rv.layoutManager = LinearLayoutManager(context)
            val adapter = reviewList.value?.let { it1 -> MyReviewRecycleViewAdapter(it1.toList()) }
            rv.adapter = adapter
        }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.edit_button).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }
}
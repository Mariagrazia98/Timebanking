package it.polito.timebanking

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import it.polito.timebanking.model.Review

class MyReviewRecycleViewAdapter(data: List<Review>) : RecyclerView.Adapter<MyReviewRecycleViewAdapter.ReviewViewHolder>(){

    var list = data

    class ReviewViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val reviewText: TextView = v.findViewById(R.id.reviewText)
        private val rating : RatingBar = v.findViewById(R.id.ratingBarDisplay)
        fun bind(item: Review) {
            reviewText.text = item.comment
            rating.rating = item.rating
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val vg = LayoutInflater.from(parent.context).inflate(R.layout.fragment_single_review, parent, false)
        return ReviewViewHolder(vg)
    }


    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val item = list[position]
        item.let { holder.bind(it) }
    }


    override fun getItemCount(): Int = list.size

}
package it.polito.timebanking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.polito.timebanking.model.Review

class MyReviewRecycleViewAdapter(data: List<Review>) : RecyclerView.Adapter<MyReviewRecycleViewAdapter.ReviewViewHolder>(){

    var list = data

    class ReviewViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val reviewText: TextView = v.findViewById(R.id.reviewText)
        private val rating : RatingBar = v.findViewById(R.id.ratingBarDisplay)
        private val date: TextView = v.findViewById(R.id.dateReview)
        private val name: TextView = v.findViewById(R.id.nameReviewer)
        private val type: TextView = v.findViewById(R.id.typeReview)
        private val image: ImageView = v.findViewById(R.id.reviewImage)

        fun bind(item: Review) {
            reviewText.text = item.comment
            rating.rating = item.rating
            date.text = item.date
            name.text = item.nameReviewer
            if(item.type == 1){
                type.text = "as Receiver"
                image.setImageResource(R.drawable.student)
            }
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
package it.polito.timebanking

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import it.polito.timebanking.viewmodel.ProfileViewModel
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import it.polito.timebanking.viewmodel.ReviewViewModel

class ShowProfileFragment : Fragment(R.layout.fragment_show_profile) {
    lateinit var fullnameView: TextView
    lateinit var ageView: TextView
    lateinit var nicknameView: TextView
    lateinit var emailView: TextView
    lateinit var locationView: TextView
    lateinit var skillsGroup: ChipGroup
    lateinit var descriptionView: TextView
    lateinit var imageView: ImageView
    lateinit var frameLayout: FrameLayout
    lateinit var nicknameTextView: TextView
    lateinit var locationTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var ratingBar: RatingBar
    lateinit var numReviews: TextView
    lateinit var avgRatings: TextView
    lateinit var cv: CardView
    lateinit var alertNoReviews: TextView

    var h: Int = 0
    var w: Int = 0
    lateinit var userId: String
    var read_only = false
    lateinit var profileVM: ProfileViewModel
    lateinit var reviewsVM: ReviewViewModel
    var id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if(read_only) {
            menu.findItem(R.id.edit_button).isVisible = false
            super.onCreateOptionsMenu(menu, inflater)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileVM = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        reviewsVM = ViewModelProvider(requireActivity()).get(ReviewViewModel::class.java)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val sv = view.findViewById<ScrollView>(R.id.scrollView)
            frameLayout = view.findViewById(R.id.frameLayout)

            sv.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    h = sv.height
                    w = sv.width

                    frameLayout.post {
                        frameLayout.layoutParams =
                            LinearLayout.LayoutParams(w - 2 * convertDpToPixel(16).toInt(), h / 3)
                    }
                    sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }


        read_only = arguments?.getBoolean("read_only")?:false
        userId = arguments?.getString("userId")!!
        setVariables(view)

        profileVM.getUserById(userId)
            .observe(viewLifecycleOwner, Observer { user ->
                if (user != null) {
                    fullnameView.text = user.fullname
                    ageView.text = user.age.toString()
                    emailView.text = user.email
                    if(user.nickname==""){
                        nicknameView.visibility=View.GONE
                        nicknameTextView.visibility=View.GONE
                    }
                    else{
                        nicknameView.text = user.nickname
                        nicknameTextView.visibility=View.VISIBLE
                    }
                    if(user.location==""){
                        locationView.visibility=View.GONE
                        locationTextView.visibility=View.GONE
                    }else{
                        locationView.text = user.location
                        locationTextView.visibility=View.VISIBLE
                    }
                    if(user.description==""){
                        descriptionView.visibility=View.GONE
                        descriptionTextView.visibility=View.GONE
                    }else{
                        descriptionView.text = user.description
                        descriptionTextView.visibility=View.VISIBLE
                    }

                    if (user.skills.isNotEmpty()) {
                        user.skills.forEach{
                            addChip(it.trim())
                        }
                    }

                    // Download directly from StorageReference using Glide
                    if(user.imagePath!=null)
                        Glide.with(this /* context */).load(user.imagePath).into(imageView)
                }
            })

    }

    private fun setVariables(view: View) {
        fullnameView = view.findViewById(R.id.fullName)
        ageView = view.findViewById(R.id.age)
        nicknameView = view.findViewById(R.id.nickname)
        emailView = view.findViewById(R.id.email)
        locationView = view.findViewById(R.id.location)
        skillsGroup = view.findViewById(R.id.skills)
        descriptionView = view.findViewById(R.id.description)
        imageView = view.findViewById(R.id.imageView)
        nicknameTextView=view.findViewById(R.id.nicknameText)
        locationTextView=view.findViewById(R.id.locationText)
        descriptionTextView=view.findViewById(R.id.descriptionText)
        ratingBar=view.findViewById(R.id.ratingBar)
        numReviews=view.findViewById(R.id.numReviews)
        avgRatings=view.findViewById(R.id.ratingAvg)
        cv=view.findViewById(R.id.cvLastRating)
        alertNoReviews=view.findViewById(R.id.alertNoReviews)
        reviewsVM.getReviewsByUser(userId).observe(viewLifecycleOwner){ reviews ->
            if(reviews.size != 0) {
                alertNoReviews.visibility= View.GONE
                ratingBar.visibility = View.VISIBLE
                numReviews.visibility = View.VISIBLE
                avgRatings.visibility = View.VISIBLE
                val avg = reviews.map { r -> r.rating }.average()
                ratingBar.rating = avg.toFloat()
                val text = "${reviews.size} reviews"
                numReviews.text = text
                avgRatings.text = avg.toString()
            }
            else{
                ratingBar.visibility = View.GONE
                numReviews.visibility = View.GONE
                avgRatings.visibility = View.GONE
                cv.visibility = View.GONE
                alertNoReviews.visibility= View.VISIBLE
            }
        }
    }

    fun convertDpToPixel(dp: Int): Float {
        return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            frameLayout.post {
                frameLayout.layoutParams =
                    LinearLayout.LayoutParams(w - 2 * convertDpToPixel(16).toInt(), h / 3)
            }
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            frameLayout.post { frameLayout.layoutParams = LinearLayout.LayoutParams(w / 3, h) }
        }
    }

    private fun addChip(text: String) {
        val chip = Chip(this.context)
        chip.text = text
        chip.isCloseIconVisible = false
        chip.chipBackgroundColor =
            this.context?.let { ContextCompat.getColor(it, R.color.primary_light) }?.let {
                ColorStateList.valueOf(it)
            }
        chip.setTextColor( this.context?.let { ContextCompat.getColor(it, R.color.primary_text) }?.let {
            ColorStateList.valueOf(it)
        })
        chip.setOnCloseIconClickListener {
            skillsGroup.removeView(chip)
        }
        skillsGroup.addView(chip)
    }
}
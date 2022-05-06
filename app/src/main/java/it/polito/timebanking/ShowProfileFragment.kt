package it.polito.timebanking

import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import it.polito.timebanking.repository.User
import it.polito.timebanking.viewmodel.ProfileViewModel
import java.io.File
import androidx.lifecycle.Observer

class ShowProfileFragment : Fragment(R.layout.fragment_show_profile) {
    private var bitmap: Bitmap? = null

    lateinit var fullnameView: TextView
    lateinit var ageView: TextView
    lateinit var nicknameView: TextView
    lateinit var emailView: TextView
    lateinit var locationView: TextView
    lateinit var skillsGroup: ChipGroup
    lateinit var descriptionView: TextView
    lateinit var imageView: ImageView
    lateinit var frameLayout: FrameLayout
    var h: Int = 0
    var w: Int = 0
    lateinit var userId: String

    lateinit var profileVM: ProfileViewModel
    lateinit var user: User
    var id: Long = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileVM = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)

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

        setVariables(view)


        userId = arguments?.getString("id")!!
        profileVM.getUserByIdF(userId)
            .observe(viewLifecycleOwner, Observer { user ->
                if (user != null) {
                    fullnameView.text = user.fullname
                    ageView.text = user.age.toString()
                    nicknameView.text = user.nickname
                    emailView.text = user.email
                    locationView.text = user.location
                    descriptionView.text = user.description
                    if (user.skills != "") {
                        user.skills.split(",").map {
                            addChip(it.trim())
                        }
                    }
                }
            })

        getProfileImageLFS()

        if (bitmap != null)
            imageView.setImageBitmap(bitmap)

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

    fun getProfileImageLFS() {
        //Get profile image from internal storage (local filesystem)
        val wrapper = ContextWrapper(requireActivity().applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "profileImage.jpg")
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(file.absolutePath)
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
        chip.setOnCloseIconClickListener {
            skillsGroup.removeView(chip)
        }
        skillsGroup.addView(chip)
    }
}
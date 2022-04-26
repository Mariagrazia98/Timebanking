package it.polito.timebanking

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import it.polito.timebanking.repository.Slot
import it.polito.timebanking.repository.User
import it.polito.timebanking.viewmodel.ProfileViewModel
import it.polito.timebanking.viewmodel.TimeSlotViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    lateinit var fullname: String
    lateinit var nickname: String
    var age = 24
    lateinit var email: String
    lateinit var location: String
    lateinit var skills: String
    val skillsList: MutableList<String> = mutableListOf()
    lateinit var description: String
    private var bitmap: Bitmap? = null

    lateinit var fullnameView: EditText
    lateinit var ageView: EditText
    lateinit var nicknameView: EditText
    lateinit var emailView: EditText
    lateinit var locationView: EditText
    lateinit var skillsView: TextView
    lateinit var descriptionView: EditText
    lateinit var frameLayout: FrameLayout

    var h: Int = 0
    var w: Int = 0

    lateinit var profileVM: ProfileViewModel
    lateinit var user: User
    var profileId:Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileId = arguments?.getInt("id")!!
        profileVM =  ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)

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

        profileVM.getUserById(profileId)?.observe(viewLifecycleOwner) {
            if(it != null) {
                println("a caso")
                fullnameView.setText(it.fullname)
                nicknameView.setText(it.nickname)
                emailView.setText(it.email)
                locationView.setText(it.location)
            }
        }

        //get extras
        /*
        fullname = "Mario Rossi"

        nickname = "mario98"
        email = "mario.rossi@gmail.com"
        location = "Torino"
        */
        age = 24
        skills = "Android Developer, Electrician"

        skills.split(",").map { skillsList.add(it) }
        skillsList.remove("")

        description = "Prova"
        getProfileImageLFS()

        fullnameView = view.findViewById(R.id.Edit_FullName)
        fullnameView.setText(fullname)
        ageView = view.findViewById(R.id.edit_age)
        ageView.setText(age.toString())
        nicknameView = view.findViewById(R.id.Edit_Nickname)
        nicknameView.setText(nickname)
        emailView = view.findViewById(R.id.Edit_Email)
        emailView.setText(email)
        locationView = view.findViewById(R.id.Edit_Location)
        locationView.setText(location)
        skillsView = view.findViewById(R.id.edit_skills)
        skillsView.text = skills
        descriptionView = view.findViewById(R.id.edit_description)
        descriptionView.setText(description)

        val iv = view.findViewById<ImageView>(R.id.Edit_imageView)
        if (bitmap != null)
            iv.setImageBitmap(bitmap)

        val imgButton = view.findViewById<Button>(R.id.imageButton)

        imgButton.setOnClickListener { //To register the button with context menu.
            registerForContextMenu(imgButton)
            requireActivity().openContextMenu(imgButton)
        }

        val skillsAddButton = view.findViewById<Button>(R.id.skillsAddButton)
        val skillsDeleteButton = view.findViewById<Button>(R.id.skillsDeleteButton)
        val addSkillView = view.findViewById<EditText>(R.id.add_skills)

        skillsAddButton.setOnClickListener {
            skillsList.add(addSkillView.text.toString())
            addSkillView.setText("")
            skillsView.text = skillsList.joinToString(separator = " • ")
        }

        skillsDeleteButton.setOnClickListener {
            skillsList.clear()
            addSkillView.setText("")
            skillsView.text = ""
        }

        addSkillView.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                skillsAddButton.isEnabled = s.toString().trim().isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })


        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Do custom work here
                    user = User()
                    user.id = profileId
                    user.fullname = fullnameView.text.toString()
                    user.nickname = nicknameView.text.toString()
                    user.email = emailView.text.toString()
                    user.location = locationView.text.toString()

                    profileVM.updateUser(user)
                    //println(id)
                    // if you want onBackPressed() to be called as normal afterwards
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
            )

    }


    fun convertDpToPixel(dp: Int): Float {
        return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }


    fun saveProfileImageLFS() {
        //Save profile image into internal storage
        val wrapper = ContextWrapper(requireActivity().applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "profileImage.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
            Toast.makeText(
                requireActivity().applicationContext,
                "Upload photo successfully!",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getProfileImageLFS() {
        //Get profile image from internal storage (local filesystem)
        val wrapper = ContextWrapper(requireActivity().applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "profileImage.jpg")
        bitmap = BitmapFactory.decodeFile(file.absolutePath)
    }

}
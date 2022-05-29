package it.polito.timebanking

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.*
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import it.polito.timebanking.model.User
import it.polito.timebanking.viewmodel.ProfileViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    var skillsList: ArrayList<String> = arrayListOf()
    private var bitmap: Bitmap? = null
    private var currentPhotoPath: String? = null

    lateinit var profileVM: ProfileViewModel
    lateinit var user: User
    lateinit var userId: String
    lateinit var fv: View

    lateinit var credit:TextView
    lateinit var fullnameView: EditText
    lateinit var ageView: EditText
    lateinit var nicknameView: EditText
    lateinit var emailView: EditText
    lateinit var locationView: EditText
    lateinit var descriptionView: EditText
    lateinit var frameLayout: FrameLayout
    lateinit var skillsGroup: ChipGroup
    lateinit var skillsAddButton: Button
    lateinit var addSkillView: EditText
    lateinit var profileImageView: ImageView
    lateinit var updateProfileButton: Button
    lateinit var skillsError : TextView
    lateinit var headerView:View

    var h: Int = 0
    var w: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        //delete edit_button
        menu.findItem(R.id.edit_button).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileVM = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        fv = view

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

        userId = arguments?.getString("userId")!!
        profileVM.getUserById(userId)

            .observe(viewLifecycleOwner, Observer {
                if (it != null && savedInstanceState == null) {
                    user = it
                    currentPhotoPath = it.imagePath

                } else if (savedInstanceState != null) {

                    user = profileVM.getUser()
                    currentPhotoPath = savedInstanceState.getString("imagePath")

                }

                fullnameView.setText(user.fullname)
                ageView.setText(user.age.toString())
                nicknameView.setText(user.nickname)
                emailView.setText(user.email)
                locationView.setText(user.location)
                descriptionView.setText(user.description)
                if (user.skills.isNotEmpty()) {
                    user.skills.forEach {skill->
                        skillsList.add(skill.trim())
                        addChip(skill.trim())
                    }
                }
                Glide.with(requireContext()).load(user.imagePath).into(profileImageView)

            })

        val imgButton = view.findViewById<ImageButton>(R.id.imageButton)

        imgButton.setOnClickListener { //To register the button with context menu.
            registerForContextMenu(imgButton)
            requireActivity().openContextMenu(imgButton)
        }

        skillsAddButton.setOnClickListener {
            if (!addSkillView.text.toString()
                    .isEmpty() && !skillsList.contains(addSkillView.text.toString())
            ) {
                addChip(addSkillView.text.toString())
                skillsList.add(addSkillView.text.toString())
            } else {
                //if skill already added
                if (skillsList.contains(addSkillView.text.toString())) {
                    val snackbar =
                        Snackbar.make(requireView(), "Skill already added!", Snackbar.LENGTH_SHORT)
                    val sbView: View = snackbar.view
                    this.context?.let { it1 -> ContextCompat.getColor(it1, R.color.danger) }
                        ?.let { it2 -> sbView.setBackgroundColor(it2) }

                    this.context?.let { it1 -> ContextCompat.getColor(it1, R.color.primary_text) }
                        ?.let { it2 -> snackbar.setTextColor(it2) }
                    snackbar.show()
                }
            }
            addSkillView.setText("")
        }

        addSkillView.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                skillsAddButton.isEnabled = s.toString().trim().isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        handleButton()
    }


    private fun handleButton() {
        updateProfileButton.setOnClickListener {
               requireActivity().onBackPressed()
        }
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val isValid= validateProfile()
                    if(isValid) {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
                        builder.setMessage("Do you want to update the profile?")
                            .setPositiveButton("Confirm") { dialog, id ->
                                user = User(
                                    uid = userId,
                                    fullname=fullnameView.text.toString(),
                                    nickname = nicknameView.text.toString(),
                                    email = emailView.text.toString(),
                                    location = locationView.text.toString(),
                                    skills = skillsList,
                                    description = descriptionView.text.toString(),
                                    age = Integer.parseInt(ageView.text.toString()),
                                    imagePath = currentPhotoPath,
                                    credit=credit.toString().toInt()
                                )
                                profileVM.updateUser(user).observe(viewLifecycleOwner, Observer {
                                    if (it) {
                                        val snackbar = Snackbar.make(
                                            requireView(),
                                            "Profile updated!",
                                            Snackbar.LENGTH_SHORT
                                        )
                                        val sbView: View = snackbar.view
                                        context?.let {
                                            ContextCompat.getColor(
                                                it,
                                                R.color.primary_light
                                            )
                                        }
                                            ?.let { it2 -> sbView.setBackgroundColor(it2) }

                                        context?.let { it1 ->
                                            ContextCompat.getColor(
                                                it1,
                                                R.color.primary_text
                                            )
                                        }
                                            ?.let { it2 -> snackbar.setTextColor(it2) }
                                        snackbar.show()
                                        if (isEnabled) {
                                            isEnabled = false
                                            requireActivity().onBackPressed()
                                        }
                                    } else {
                                        val snackbar = Snackbar.make(
                                            requireView(),
                                            "Something is wrong, try later!",
                                            Snackbar.LENGTH_SHORT
                                        )
                                        snackbar.show()
                                    }
                                })
                            }
                            .setNegativeButton(
                                "Cancel",
                                DialogInterface.OnClickListener { dialog, id ->
                                    // User cancelled the dialog
                                    // if you want onBackPressed() to be called as normal afterwards
                                    if (isEnabled) {
                                        isEnabled = false
                                        requireActivity().onBackPressed()
                                    }
                                })
                        builder.show()
                    }
                }
            })
    }

    private fun validateProfile():Boolean {
        var isValid = true

        if(ageView.text.isEmpty() || ageView.text.toString().toInt()<16 || ageView.text.toString().toInt()>120){
            ageView.error="Age should be more than 16"
            isValid = false
        }
        else{
            ageView.error=null
        }

        if(skillsList.isEmpty()){
            skillsError.visibility = View.VISIBLE
            isValid = false
        } else{
            skillsError.visibility = View.GONE
        }

        return isValid
    }

    private fun addChip(text: String) {
        val chip = Chip(this.context)
        chip.text = text
        chip.isCloseIconVisible = true
        chip.chipBackgroundColor =
            this.context?.let { ContextCompat.getColor(it, R.color.primary_light) }?.let {
                ColorStateList.valueOf(it)
            }
        chip.setOnCloseIconClickListener {
            skillsGroup.removeView(chip)
            skillsList.remove(chip.text.toString())
        }
        skillsGroup.addView(chip)
        skillsError.visibility = View.GONE
    }

    fun setVariables(view: View) {
        credit=view.findViewById(R.id.credit)
        fullnameView = view.findViewById(R.id.Edit_FullName)
        ageView = view.findViewById(R.id.edit_age)
        nicknameView = view.findViewById(R.id.Edit_Nickname)
        emailView = view.findViewById(R.id.Edit_Email)
        locationView = view.findViewById(R.id.Edit_Location)
        descriptionView = view.findViewById(R.id.edit_description)
        skillsAddButton = view.findViewById(R.id.skillsAddButton)
        addSkillView = view.findViewById(R.id.add_skills)
        skillsGroup = view.findViewById(R.id.skills)
        profileImageView = view.findViewById(R.id.Edit_imageView)
        updateProfileButton=view.findViewById(R.id.updateProfileButton)
        headerView=(activity as MainActivity).headerView
        skillsError = view.findViewById(R.id.skillsTextError)
        skillsError.visibility = View.GONE
    }


    //CAMERA
    fun handleCameraImage(intent: Intent?) {
        bitmap = intent?.extras?.get("data") as Bitmap
        val iv = fv.findViewById<ImageView>(R.id.Edit_imageView)
        iv.setImageBitmap(bitmap)
        saveProfileImage()
    }

    fun handleGalleryImage(uri: Uri?) {
        val iv = fv.findViewById<ImageView>(R.id.Edit_imageView)
        iv.setImageURI(uri)
        bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        saveProfileImage()
    }

    //result of opening camera
    val resultLauncherCameraImage =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleCameraImage(result.data)
            }
        }

    //result of opening gallery
    val resultLauncherGalleryImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                handleGalleryImage(uri)
            }
        }

    fun openCamera() {
        //intent to open camera app
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncherCameraImage.launch(cameraIntent)
    }

    fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        resultLauncherGalleryImage.launch("image/*")
    }

    //create the floating menu after pressing on the camera img
    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate(R.menu.image_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.camera -> {
                openCamera()
                true
            }
            R.id.gallery -> {
                openGallery()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    fun convertDpToPixel(dp: Int): Float {
        return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun saveProfileImage() {
        val wrapper = ContextWrapper(requireActivity().applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        val filename = user.uid + ".jpg"
        file = File(file, filename)
        val stream: OutputStream = FileOutputStream(file)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()

        val userRef =
            Firebase.storage.reference.child("images_user/${Uri.fromFile(file).lastPathSegment}")
        userRef.putFile(Uri.fromFile(file))
            .addOnSuccessListener {
                file.delete() //delete the file

                currentPhotoPath = Uri.fromFile(file).lastPathSegment
                userRef.downloadUrl.addOnCompleteListener {
                    user.imagePath = it.result.toString() //new file path
                    currentPhotoPath = user.imagePath

                    profileVM.updateUser(user).observe(viewLifecycleOwner, Observer {
                        if (it) {
                            Glide.with(requireContext()).load(user.imagePath).into( headerView.findViewById<CircleImageView>(R.id.imageViewHeader))
                            val snackbar = Snackbar.make(
                                requireView(), "Upload photo successfully!", Snackbar.LENGTH_SHORT
                            )
                            val sbView: View = snackbar.view
                            context?.let { ContextCompat.getColor(it, R.color.primary_light) }
                                ?.let { it2 -> sbView.setBackgroundColor(it2) }

                            context?.let { it1 ->
                                ContextCompat.getColor(
                                    it1,
                                    R.color.primary_text
                                )
                            }
                                ?.let { it2 -> snackbar.setTextColor(it2) }
                            snackbar.show()
                        } else {
                            val snackbar = Snackbar.make(
                                requireView(),
                                "Error while updtaing the photo  profile! Try again!",
                                Snackbar.LENGTH_SHORT
                            )
                            snackbar.show()
                        }
                    })
                }
            }
            .addOnFailureListener {
                val snackbar = Snackbar.make(
                    requireView(),
                    "Error while updtaing the photo  profile! Try again!",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.show()
            }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        profileVM.setUser(user = User(user.uid, user.fullname,nicknameView.text.toString(), user.email, locationView.text.toString(), descriptionView.text.toString(),skillsList,  ageView.text.toString().toInt(), user.imagePath, credit = credit.toString().toInt()))
        outState.putString("imagePath", currentPhotoPath)
    }
}
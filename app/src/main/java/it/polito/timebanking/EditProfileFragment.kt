package it.polito.timebanking

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
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
import com.google.firebase.Timestamp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.polito.timebanking.model.UserFire
import it.polito.timebanking.viewmodel.ProfileViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    var fullname: String=""
    var nickname: String=""
    var age = 24
    var email: String = ""
    var location: String = ""
    var description: String = ""
    var skillsList: ArrayList<String> = arrayListOf()
    var imagePath: String?= null

    private var bitmap: Bitmap? = null
    private var currentPhotoPath: String? = null

    lateinit var fullnameView: EditText
    lateinit var ageView: EditText
    lateinit var nicknameView: EditText
    lateinit var emailView: EditText
    lateinit var locationView: EditText
    lateinit var descriptionView: EditText
    lateinit var frameLayout: FrameLayout
    lateinit var skillsGroup: ChipGroup
    lateinit var skillsAddButton:Button
    lateinit var addSkillView:EditText
    lateinit var profileImageView: ImageView
    var h: Int = 0
    var w: Int = 0

    lateinit var profileVM: ProfileViewModel
    //lateinit var user: User
    lateinit var  user:UserFire
    lateinit var userId:String
    lateinit var fv: View


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

        userId = arguments?.getString("id")!!
        profileVM =  ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
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


        userId = arguments?.getString("id")!!
        Log.d("user", userId)
        profileVM.getUserByIdF(userId)
            .observe(viewLifecycleOwner, Observer {
                if(it != null && savedInstanceState==null) {
                    user=it
                    fullname = it.fullname
                    nickname = it.nickname
                    email= it.email
                    location= it.location
                    age=it.age
                    description=it.description
                    if(it.skills != ""){
                        it.skills.split(",").map {
                            skillsList.add(it.trim())
                            addChip(it.trim())
                        }
                    }
                    currentPhotoPath=it.imagePath
                    Glide.with(requireContext()).load(user.imagePath).into(profileImageView)
                }else{
                    if (savedInstanceState != null) {
                        fullname = savedInstanceState.getString("fullname", fullname)
                        nickname = savedInstanceState.getString("nickname", nickname)
                        email = savedInstanceState.getString("email", email)
                        location = savedInstanceState.getString("location", location)
                        description = savedInstanceState.getString("description", description)
                        age = savedInstanceState.getInt("age", age)
                        skillsList = savedInstanceState.getStringArrayList("skillsList") as ArrayList<String>
                        skillsList.map{ addChip(it.trim())}
                        imagePath = savedInstanceState.getString("imagePath", imagePath)
                        currentPhotoPath = savedInstanceState.getString("currentPhotoPath")

                    }

                }


                fullnameView.setText(fullname)
                ageView.setText(age.toString())
                nicknameView.setText(nickname)
                emailView.setText(email)
                locationView.setText(location)
                descriptionView.setText(description)
                Log.d("imagePath", imagePath.toString())
                Glide.with(requireContext()).load(currentPhotoPath).into(profileImageView)

            })
        /*TODO remove it*/
        /*      profileVM.getUserById(userId)?.observe(viewLifecycleOwner) {

        }*/

/*        getProfileImageLFS(savedInstanceState)
        val iv = view.findViewById<ImageView>(R.id.Edit_imageView)
        if (bitmap != null)
            iv.setImageBitmap(bitmap)*/




        val imgButton = view.findViewById<ImageButton>(R.id.imageButton)

        imgButton.setOnClickListener { //To register the button with context menu.
            registerForContextMenu(imgButton)
            requireActivity().openContextMenu(imgButton)
        }

       skillsAddButton.setOnClickListener {
            if(!addSkillView.text.toString().isEmpty() && !skillsList.contains(addSkillView.text.toString())){
                addChip(addSkillView.text.toString())
                skillsList.add(addSkillView.text.toString())
            }else{
                //if skill already added
                if(skillsList.contains(addSkillView.text.toString())){
                    val snackbar = Snackbar.make(requireView(), "Skill already added!", Snackbar.LENGTH_SHORT)
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

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                  /*  user = User()
                    user.fullname = fullnameView.text.toString()
                    user.nickname = nicknameView.text.toString()
                    user.email = emailView.text.toString()
                    user.location = locationView.text.toString()
                    user.skills = skillsList.toString().removePrefix("[").removeSuffix("]")
                    user.description = descriptionView.text.toString()
                    user.age= Integer.parseInt(ageView.text.toString())
                    user.imagePath = getProfileImage().absolutePath
                    user.id = userId
                    profileVM.updateUser(user)

                   */
                    user = UserFire()
                    user.fullname = fullnameView.text.toString()
                    user.nickname = nicknameView.text.toString()
                    user.email = emailView.text.toString()
                    user.location = locationView.text.toString()
                    user.skills = skillsList.toString().removePrefix("[").removeSuffix("]")
                    user.description = descriptionView.text.toString()
                    user.age= Integer.parseInt(ageView.text.toString())
                    user.imagePath = currentPhotoPath
                    user.uid = userId
                    profileVM.updateUserF(user).observe(viewLifecycleOwner, Observer {
                        if(it){
                            val snackbar = Snackbar.make(requireView(), "Profile updated!", Snackbar.LENGTH_SHORT)
                            val sbView: View = snackbar.view
                            context?.let { ContextCompat.getColor(it, R.color.primary_light) }
                                ?.let { it2 -> sbView.setBackgroundColor(it2) }

                            context?.let { it1 -> ContextCompat.getColor(it1, R.color.primary_text) }
                                ?.let { it2 -> snackbar.setTextColor(it2) }
                            snackbar.show()
                        }
                        else{
                            val snackbar = Snackbar.make(requireView(), "Something is wrong, try later!", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        }
                    })

                    // if you want onBackPressed() to be called as normal afterwards
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })
    }

    private fun addChip(text: String){
        val chip = Chip(this.context)
        chip.text = text
        chip.isCloseIconVisible = true
        chip.chipBackgroundColor =
            this.context?.let { ContextCompat.getColor(it, R.color.primary_light) }?.let {
                ColorStateList.valueOf(it) }
        chip.setOnCloseIconClickListener{
            skillsGroup.removeView(chip)
            skillsList.remove(chip.text.toString())
        }
        skillsGroup.addView(chip)
    }

    fun setVariables(view: View){
        fullnameView = view.findViewById(R.id.Edit_FullName)
        ageView = view.findViewById(R.id.edit_age)
        nicknameView = view.findViewById(R.id.Edit_Nickname)
        emailView = view.findViewById(R.id.Edit_Email)
        locationView = view.findViewById(R.id.Edit_Location)
        descriptionView = view.findViewById(R.id.edit_description)
        skillsAddButton = view.findViewById(R.id.skillsAddButton)
        addSkillView = view.findViewById(R.id.add_skills)
        skillsGroup = view.findViewById(R.id.skills)
        profileImageView=view.findViewById(R.id.Edit_imageView)
    }


    //CAMERA

    fun handleCameraImage(intent: Intent?) {
        bitmap = intent?.extras?.get("data") as Bitmap
        val iv = fv.findViewById<ImageView>(R.id.Edit_imageView)
        iv.setImageBitmap(bitmap)
        saveProfileImageLFS()
    }

    fun handleGalleryImage(uri: Uri?){
        val iv = fv.findViewById<ImageView>(R.id.Edit_imageView)
        iv.setImageURI(uri)
        bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        saveProfileImageLFS()
    }

    //result of opening camera
    val resultLauncherCameraImage =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
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

    fun openCamera(){
        //intent to open camera app
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncherCameraImage.launch(cameraIntent)
    }

    fun openGallery(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        resultLauncherGalleryImage.launch("image/*")
    }

    //create the floating menu after pressing on the camera img
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
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

    fun saveProfileImageLFS() {
        //Save profile image into internal storage
        val wrapper = ContextWrapper(requireActivity().applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        val filename = user.uid + Timestamp.now()+".jpg"
        file = File(file, filename)
        currentPhotoPath = file.absolutePath
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()


            val userRef = Firebase.storage.reference.child("images_user/${Uri.fromFile(file).lastPathSegment }")
            userRef.putFile(Uri.fromFile(file))
                .addOnSuccessListener {
                    file.delete() //delete the file from local storage
                    currentPhotoPath = Uri.fromFile(file).lastPathSegment
                    userRef.downloadUrl.addOnCompleteListener {
                        user.imagePath = it.result.toString()
                        currentPhotoPath = user.imagePath
                        Log.d("ATTEMPT","Path"+ user.imagePath.toString())
                        profileVM.updateUserF(user).observe(viewLifecycleOwner, Observer {
                            Log.d("ATTEMPT","Booelan"+it)
                            if(it){
                                val snackbar = Snackbar.make(requireView(), "Upload photo successfully!", Snackbar.LENGTH_SHORT)
                                val sbView: View = snackbar.view
                                context?.let { ContextCompat.getColor(it, R.color.primary_light) }
                                    ?.let { it2 -> sbView.setBackgroundColor(it2) }

                                context?.let { it1 -> ContextCompat.getColor(it1, R.color.primary_text) }
                                    ?.let { it2 -> snackbar.setTextColor(it2) }
                                snackbar.show()
                            }
                            else{
                                Log.d("LOG","something goes wrong")
                            }
                        })

                    }
                }
                .addOnFailureListener {
                    val snackbar = Snackbar.make(requireView(), "Error while updtaing the photo  profile! Try again!", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }


        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

   /* fun getProfileImage(): File{
        //Get profile image from internal storage (local filesystem)
        val wrapper = ContextWrapper(requireActivity().applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        return File(file, "profileImage.jpg")

    }*/

   /* fun getProfileImageLFS( savedInstanceState: Bundle?) {
        if(savedInstanceState != null){
            //bitmap = savedInstanceState.getParcelable("bitmap")
        }
        else if(getProfileImage().exists()) {
            bitmap = BitmapFactory.decodeFile(getProfileImage().absolutePath)
        }
    }*/

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("skillsList", skillsList)
        outState.putString("fullname", fullnameView.text.toString())
        outState.putString("nickname", nicknameView.text.toString())
        outState.putInt("age", ageView.text.toString().toInt())
        outState.putString("email", emailView.text.toString())
        outState.putString("location", locationView.text.toString())
        outState.putString("description", descriptionView.text.toString())
        outState.putString("currentPhotoPath", currentPhotoPath)
        outState.putString("imagePath", currentPhotoPath)
    }
}
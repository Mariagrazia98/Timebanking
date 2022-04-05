package it.polito.showprofileactivity

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
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class EditProfileActivity : AppCompatActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val sv = findViewById<ScrollView>(R.id.scrollView)
            frameLayout = findViewById(R.id.frameLayout)

            sv.viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    h = sv.height
                    w = sv.width
                    frameLayout.post {
                        frameLayout.layoutParams = LinearLayout.LayoutParams(w, h / 3)
                    }

                    sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }

        ///get extras
        val i = intent
        fullname = i.getStringExtra("group09.lab1.FULL_NAME").toString()
        age = i.getStringExtra("group09.lab1.AGE").toString().toInt()
        nickname = i.getStringExtra("group09.lab1.NICKNAME").toString()
        email = i.getStringExtra("group09.lab1.EMAIL").toString()
        location = i.getStringExtra("group09.lab1.LOCATION").toString()
        skills = i.getStringExtra("group09.lab1.SKILLS").toString()

        skills.split(",").map { skillsList.add(it) }

        description = i.getStringExtra("group09.lab1.DESCRIPTION").toString()
        getProfileImageLFS()

        fullnameView = findViewById(R.id.Edit_FullName)
        fullnameView.setText(fullname)
        ageView = findViewById(R.id.edit_age)
        ageView.setText(age.toString())
        nicknameView = findViewById(R.id.Edit_Nickname)
        nicknameView.setText(nickname)
        emailView = findViewById(R.id.Edit_Email)
        emailView.setText(email)
        locationView = findViewById(R.id.Edit_Location)
        locationView.setText(location)
        skillsView = findViewById(R.id.edit_skills)
        skillsView.text = skills
        descriptionView = findViewById(R.id.edit_description)
        descriptionView.setText(description)

        val iv = findViewById<ImageView>(R.id.Edit_imageView)
        if (bitmap != null)
            iv.setImageBitmap(bitmap)

        val imgButton = findViewById<Button>(R.id.imageButton)

        imgButton.setOnClickListener { //To register the button with context menu.
            registerForContextMenu(imgButton)
            openContextMenu(imgButton)
        }

        val skillsAddButton = findViewById<Button>(R.id.skillsAddButton)
        val skillsDeleteButton = findViewById<Button>(R.id.skillsDeleteButton)
        val addSkillView = findViewById<EditText>(R.id.add_skills)

        skillsAddButton.setOnClickListener {
            skillsList.add(addSkillView.text.toString())
            addSkillView.setText("")
            skillsView.text = skillsList.joinToString()
        }

        skillsDeleteButton.setOnClickListener {
            skillsList.clear()
            addSkillView.setText("")
            skillsView.text = ""
        }

        addSkillView.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                skillsAddButton.isEnabled = s.toString().trim().isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
        })

    }

    override fun onBackPressed() {
        val i = intent
        fullname = findViewById<EditText>(R.id.Edit_FullName).text.toString()
        age = findViewById<EditText>(R.id.edit_age).text.toString().toInt()
        nickname = findViewById<EditText>(R.id.Edit_Nickname).text.toString()
        email =  findViewById<EditText>(R.id.Edit_Email).text.toString()
        location = findViewById<EditText>(R.id.Edit_Location).text.toString()
        skills = findViewById<TextView>(R.id.edit_skills).text.toString()
        description = findViewById<EditText>(R.id.edit_description).text.toString()

        i.putExtra("group09.lab1.FULL_NAME", fullname)
        i.putExtra("group09.lab1.AGE", age.toString())
        i.putExtra("group09.lab1.NICKNAME", nickname)
        i.putExtra("group09.lab1.EMAIL", email)
        i.putExtra("group09.lab1.LOCATION", location)
        i.putExtra("group09.lab1.SKILLS", skills)
        i.putExtra("group09.lab1.DESCRIPTION", description)

        setResult(Activity.RESULT_OK, i)
        super.onBackPressed() // to call at the end, because it calls internally the finish() method
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            frameLayout.post{frameLayout.layoutParams = LinearLayout.LayoutParams(w, h/3)}
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            frameLayout.post{frameLayout.layoutParams = LinearLayout.LayoutParams(w/3, h)}
        }
    }

    //create the floating menu after pressing on the camera img
    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
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

    //result of opening camera
    val resultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    handleCameraImage(result.data)
                }
            }
    private val resultLauncherGalleryImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val iv = findViewById<ImageView>(R.id.Edit_imageView)
                iv.setImageURI(uri)
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                saveProfileImageLFS()

            }
        }
    private fun openCamera(){
        //intent to open camera app
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(cameraIntent)
    }
    private fun openGallery(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        resultLauncherGalleryImage.launch("image/*")
    }
  
    private fun handleCameraImage(intent: Intent?) {
        bitmap = intent?.extras?.get("data") as Bitmap
        val iv = findViewById<ImageView>(R.id.Edit_imageView)
        iv.setImageBitmap(bitmap)
        saveProfileImageLFS()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("skillsList",  skillsList.joinToString())

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val skillsList = savedInstanceState.getString("skillsList")
        skillsView.text =  skillsList

    }
    fun saveProfileImageLFS(){
        //Save profile image into internal storage
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "profileImage.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException){
            e.printStackTrace()
        }
    }

    fun getProfileImageLFS(){
        //Get profile image from internal storage (local filesystem)
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "profileImage.jpg")
        bitmap = BitmapFactory.decodeFile(file.absolutePath)

    }
}
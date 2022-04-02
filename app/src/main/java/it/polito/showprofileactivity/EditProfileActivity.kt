package it.polito.showprofileactivity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Button
import android.widget.EditText

class EditProfileActivity : AppCompatActivity() {
    lateinit var fullname: String
    lateinit var nickname: String
    var age = 24
    lateinit var email: String
    lateinit var location: String
    lateinit var skills: String
    lateinit var description: String
    private var bitmap: Bitmap? = null


    lateinit var fullnameView: EditText
    lateinit var ageView: EditText
    lateinit var nicknameView: EditText
    lateinit var emailView: EditText
    lateinit var locationView: EditText
    lateinit var skillsView: EditText
    lateinit var descriptionView: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        ///get extras
        val i = intent
        fullname = i.getStringExtra("group09.lab1.FULL_NAME").toString()
        age = i.getStringExtra("group09.lab1.AGE").toString().toInt()
        nickname = i.getStringExtra("group09.lab1.NICKNAME").toString()
        email = i.getStringExtra("group09.lab1.EMAIL").toString()
        location = i.getStringExtra("group09.lab1.LOCATION").toString()
        skills = i.getStringExtra("group09.lab1.SKILLS").toString()
        description = i.getStringExtra("group09.lab1.DESCRIPTION").toString()
        bitmap = i.getParcelableExtra("group09.lab1.PROFILE_IMAGE")

        fullnameView = findViewById(R.id.Edit_FullName)
        fullnameView.setText(fullname)
        ageView = findViewById(R.id.edit_age)
        ageView.setText(age.toString())
        nicknameView = findViewById(R.id.Edit_Nickname)
        nicknameView.setText(nickname)
        emailView =  findViewById(R.id.Edit_Email)
        emailView.setText(email)
        locationView = findViewById(R.id.Edit_Location)
        locationView.setText(location)
        skillsView = findViewById(R.id.edit_skills)
        skillsView.setText(skills)
        descriptionView = findViewById(R.id.edit_description)
        descriptionView.setText(description)

        val iv = findViewById<ImageView>(R.id.Edit_imageView)
        if(bitmap!=null)
            iv.setImageBitmap(bitmap)

        val imgButton = findViewById<Button>(R.id.imageButton)
        registerForContextMenu(imgButton)
     }

    override fun onBackPressed() {
        val i = intent
        fullname = findViewById<EditText>(R.id.Edit_FullName).text.toString()
        age = findViewById<EditText>(R.id.edit_age).text.toString().toInt()
        nickname = findViewById<EditText>(R.id.Edit_Nickname).text.toString()
        email =  findViewById<EditText>(R.id.Edit_Email).text.toString()
        location = findViewById<EditText>(R.id.Edit_Location).text.toString()
        skills = findViewById<EditText>(R.id.edit_skills).text.toString()
        description = findViewById<EditText>(R.id.edit_description).text.toString()

        i.putExtra("group09.lab1.FULL_NAME", fullname)
        i.putExtra("group09.lab1.AGE", age.toString())
        i.putExtra("group09.lab1.NICKNAME", nickname)
        i.putExtra("group09.lab1.EMAIL", email)
        i.putExtra("group09.lab1.LOCATION", location)
        i.putExtra("group09.lab1.SKILLS", skills)
        i.putExtra("group09.lab1.DESCRIPTION", description)
        i.putExtra("group09.lab1.IMAGE", bitmap)

        setResult(Activity.RESULT_OK, i)
        super.onBackPressed() // to call at the end, because it calls internally the finish() method
    }


    //create the floating menu after pressing on the camera img
    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo?) {
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
                //open gallery
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

    private fun openCamera(){
        //intent to open camera app
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(cameraIntent)
    }

    private fun handleCameraImage(intent: Intent?) {
        bitmap = intent?.extras?.get("data") as Bitmap
        val iv = findViewById<ImageView>(R.id.Edit_imageView)
        iv.setImageBitmap(bitmap)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("bitmap",bitmap)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bitmap = savedInstanceState.getParcelable("bitmap")
        val iv = findViewById<ImageView>(R.id.Edit_imageView)
        if(bitmap!=null)
            iv.setImageBitmap(bitmap)
    }

}
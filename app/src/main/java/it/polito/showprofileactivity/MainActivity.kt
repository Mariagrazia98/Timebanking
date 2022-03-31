package it.polito.showprofileactivity

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import org.json.JSONObject
import java.io.*


class MainActivity : AppCompatActivity() {
    lateinit var fullname: String
    lateinit var nickname: String
    var age = 24
    lateinit var email: String
    lateinit var location: String
    lateinit var skills: String
    lateinit var description: String
    private var bitmap: Bitmap? = null
    lateinit var sharedPref : SharedPreferences

    lateinit var fullnameView: TextView
    lateinit var ageView: TextView
    lateinit var nicknameView: TextView
    lateinit var emailView: TextView
    lateinit var locationView: TextView
    lateinit var skillsView: TextView
    lateinit var descriptionView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fullnameView = findViewById(R.id.fullName)
        ageView = findViewById(R.id.age)
        nicknameView = findViewById(R.id.nickname)
        emailView = findViewById(R.id.email)
        locationView = findViewById(R.id.location)
        skillsView = findViewById(R.id.skills)
        descriptionView = findViewById(R.id.description)

        getInfoSP()
        getProfileImageLFS()

        val iv = findViewById<ImageView>(R.id.imageView)
        if(bitmap!=null)
            iv.setImageBitmap(bitmap)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.edit_button, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.edit_button -> {
                editProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

     val resultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                fullnameView.text = result.data?.getStringExtra("group09.lab1.FULL_NAME")
                ageView.text = result.data?.getStringExtra("group09.lab1.AGE")
                nicknameView.text = result.data?.getStringExtra("group09.lab1.NICKNAME")
                emailView.text = result.data?.getStringExtra("group09.lab1.EMAIL")
                locationView.text = result.data?.getStringExtra("group09.lab1.LOCATION")
                skillsView.text = result.data?.getStringExtra("group09.lab1.SKILLS")
                descriptionView.text = result.data?.getStringExtra("group09.lab1.DESCRIPTION")


                fullname = result.data?.getStringExtra("group09.lab1.FULL_NAME").toString()
                age = result.data?.getStringExtra("group09.lab1.AGE").toString().toInt()
                nickname = result.data?.getStringExtra("group09.lab1.NICKNAME").toString()
                email = result.data?.getStringExtra("group09.lab1.EMAIL").toString()
                location = result.data?.getStringExtra("group09.lab1.LOCATION").toString()
                skills = result.data?.getStringExtra("group09.lab1.SKILLS").toString()
                description=result.data?.getStringExtra("group09.lab1.DESCRIPTION").toString()
                bitmap = result.data?.getParcelableExtra("group09.lab1.IMAGE")

                val iv = findViewById<ImageView>(R.id.imageView)
                if(bitmap!=null)
                    iv.setImageBitmap(bitmap)

                storeInfoSP()
                saveProfileImageLFS()
            }
        }

    private fun editProfile(){
        //call edit activity
        val intent = Intent(this, EditProfileActivity::class.java)

        intent.putExtra("group09.lab1.FULL_NAME", fullnameView.text)
        intent.putExtra("group09.lab1.AGE", ageView.text)
        intent.putExtra("group09.lab1.NICKNAME", nicknameView.text)
        intent.putExtra("group09.lab1.EMAIL", emailView.text)
        intent.putExtra("group09.lab1.LOCATION", locationView.text)
        intent.putExtra("group09.lab1.SKILLS", skillsView.text)
        intent.putExtra("group09.lab1.DESCRIPTION", descriptionView.text)
        intent.putExtra("group09.lab1.PROFILE_IMAGE", bitmap)
        resultLauncher.launch(intent)
    }


    fun storeInfoSP(){
        //Save data to SharedPreferences in a JSON object
        val jsonProfile = JSONObject()
        jsonProfile.put("fullname", fullname)
        jsonProfile.put("age", age)
        jsonProfile.put("nickname", nickname)
        jsonProfile.put("email", email)
        jsonProfile.put("location", location)
        jsonProfile.put("skills", skills)
        jsonProfile.put("description", description)

        val editor = sharedPref.edit()
        editor.putString("profile", jsonProfile.toString()) //sharedPref saves (key,value) pair and this method wants a string as value
        editor.apply()
    }

    fun getInfoSP(){
        //Get data from SharedPreferences
        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        //Create a json with default data to be used if sharedPref doesn't contain anything
        val jsonDefault = JSONObject()
        jsonDefault.put("fullname", "Mario Rossi")
        jsonDefault.put("age", 24)
        jsonDefault.put("nickname", "Mario98")
        jsonDefault.put("email", "mario.rossi@gmail.com")
        jsonDefault.put("location", "Torino")
        jsonDefault.put("skills", "Android developer, Electrician")
        jsonDefault.put("description", "Frequento il Politecnico di Torino")

        val profileString = sharedPref.getString("profile", jsonDefault.toString()) //retrieve the string containing data in json format with the key "profile"
        val json = JSONObject(profileString.toString())  //transform the obtained string into a json to easily access all the fields

        if(json.has("fullname"))
            fullname = json.getString("fullname")
        else
            fullname = jsonDefault.getString("fullname")
        if(json.has("age"))
            age = json.getInt("age")
        else
            age = jsonDefault.getInt("age")
        if(json.has("nickname"))
            nickname = json.getString("nickname")
        else
            nickname = jsonDefault.getString("nickname")
        if(json.has("email"))
            email = json.getString("email")
        else
            email = jsonDefault.getString("email")
        if(json.has("location"))
            location = json.getString("location")
        else
            location = jsonDefault.getString("location")
        if(json.has("skills"))
            skills = json.getString("skills")
        else
            skills = jsonDefault.getString("skills")
        if(json.has("description"))
            description = json.getString("description")
        else
            description = jsonDefault.getString("description")

        fullnameView.text = fullname
        ageView.text = age.toString()
        nicknameView.text = nickname
        emailView.text = email
        locationView.text = location
        skillsView.text = skills
        descriptionView.text=description
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("bitmap",bitmap)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bitmap = savedInstanceState.getParcelable("bitmap")
        val iv = findViewById<ImageView>(R.id.imageView)
        if(bitmap!=null)
            iv.setImageBitmap(bitmap)
    }

}
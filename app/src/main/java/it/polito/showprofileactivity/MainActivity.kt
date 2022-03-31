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
    lateinit var description: String
    private var bitmap: Bitmap? = null
    lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fullnameView = findViewById<TextView>(R.id.fullName)
        val ageView = findViewById<TextView>(R.id.age)
        val nicknameView = findViewById<TextView>(R.id.nickname)
        val emailView = findViewById<TextView>(R.id.email)
        val locationView = findViewById<TextView>(R.id.location)
        val descriptionView = findViewById<TextView>(R.id.description)

        //Get data from SharedPreferences
        sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        //Create a json with default data to be used if sharedPref doesn't contain anything
        val jsonDefault = JSONObject()
        jsonDefault.put("fullname", "Mario Rossi")
        jsonDefault.put("age", 24)
        jsonDefault.put("nickname", "Mario98")
        jsonDefault.put("email", "mario.rossi@gmail.com")
        jsonDefault.put("location", "Torino")
        jsonDefault.put("description", "Frequento il Politecnico di Torino")

        val profileString = sharedPref.getString("profile", jsonDefault.toString()) //retrieve the string containing data in json format with the key "profile"
        val json = JSONObject(profileString.toString())  //transform the obtained string into a json to easily access all the fields
       

        if(json.has("fullname"))
            fullname = json.getString("fullname")
        if(json.has("age"))
            age = json.getInt("age")
        if(json.has("nickname"))
            nickname = json.getString("nickname")
        if(json.has("email"))
            email = json.getString("email")
        if(json.has("location"))
            location = json.getString("location")
        if(json.has("description"))
            description = json.getString("description")
        else{
            description = jsonDefault.getString("description")
        }

        fullnameView.text = fullname
        ageView.text = age.toString()
        nicknameView.text = nickname
        emailView.text = email
        locationView.text = location
        descriptionView.text=description

        //Get profile image from internal storage (local filesystem)
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "profileImage.jpg")
        bitmap = BitmapFactory.decodeFile(file.absolutePath)

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
                val fullnameView = findViewById<TextView>(R.id.fullName)
                fullnameView.text = result.data?.getStringExtra("group09.lab1.FULL_NAME")

                val ageView = findViewById<TextView>(R.id.age)
                ageView.text = result.data?.getStringExtra("group09.lab1.AGE")

                val nicknameView = findViewById<TextView>(R.id.nickname)
                nicknameView.text = result.data?.getStringExtra("group09.lab1.NICKNAME")

                val emailView =  findViewById<TextView>(R.id.email)
                emailView.text = result.data?.getStringExtra("group09.lab1.EMAIL")

                val locationView = findViewById<TextView>(R.id.location)
                locationView.text = result.data?.getStringExtra("group09.lab1.LOCATION")

                val descriptionView = findViewById<TextView>(R.id.description)
                descriptionView.text = result.data?.getStringExtra("group09.lab1.DESCRIPTION")


                fullname = result.data?.getStringExtra("group09.lab1.FULL_NAME").toString()
                age = result.data?.getStringExtra("group09.lab1.AGE").toString().toInt()
                nickname = result.data?.getStringExtra("group09.lab1.NICKNAME").toString()
                email = result.data?.getStringExtra("group09.lab1.EMAIL").toString()
                location = result.data?.getStringExtra("group09.lab1.LOCATION").toString()
                description=result.data?.getStringExtra("group09.lab1.DESCRIPTION").toString()
                bitmap = result.data?.getParcelableExtra("group09.lab1.IMAGE")


                val iv = findViewById<ImageView>(R.id.imageView)
                if(bitmap!=null)
                    iv.setImageBitmap(bitmap)

                //Save data to SharedPreferences in a JSON object
                val jsonProfile = JSONObject()
                jsonProfile.put("fullname", fullname)
                jsonProfile.put("age", age)
                jsonProfile.put("nickname", nickname)
                jsonProfile.put("email", email)
                jsonProfile.put("location", location)
                jsonProfile.put("description", description)

                val editor = sharedPref.edit()
                editor.putString("profile", jsonProfile.toString()) //sharedPref saves (key,value) pair and this method wants a string as value
                editor.apply()

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
        }

    private fun editProfile(){
        //call edit activity
        val intent = Intent(this, EditProfileActivity::class.java)
        val fullnameView = findViewById<TextView>(R.id.fullName)
        val ageView = findViewById<TextView>(R.id.age)
        val nicknameView = findViewById<TextView>(R.id.nickname)
        val emailView =  findViewById<TextView>(R.id.email)
        val locationView = findViewById<TextView>(R.id.location)
        val descriptionView = findViewById<TextView>(R.id.description)

        intent.putExtra("group09.lab1.FULL_NAME", fullnameView.text)
        intent.putExtra("group09.lab1.AGE", ageView.text)
        intent.putExtra("group09.lab1.NICKNAME", nicknameView.text)
        intent.putExtra("group09.lab1.EMAIL", emailView.text)
        intent.putExtra("group09.lab1.LOCATION", locationView.text)
        intent.putExtra("group09.lab1.DESCRIPTION", descriptionView.text)
        intent.putExtra("group09.lab1.PROFILE_IMAGE", bitmap)
        resultLauncher.launch(intent)
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
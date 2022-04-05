package it.polito.showprofileactivity

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


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
    lateinit var imageView : ImageView
    lateinit var frameLayout : FrameLayout
    var h: Int = 0
    var w: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val sv = findViewById<ScrollView>(R.id.scrollView)
            frameLayout = findViewById(R.id.frameLayout)

            sv.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        h = sv.height
                        w = sv.width
                        frameLayout.post{frameLayout.layoutParams = LinearLayout.LayoutParams(w, h/3)}

                        sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
            })
        }

        fullnameView = findViewById(R.id.fullName)
        ageView = findViewById(R.id.age)
        nicknameView = findViewById(R.id.nickname)
        emailView = findViewById(R.id.email)
        locationView = findViewById(R.id.location)
        skillsView = findViewById(R.id.skills)
        descriptionView = findViewById(R.id.description)
        imageView = findViewById(R.id.imageView)

        getInfoSP()
        getProfileImageLFS()

        println("Main- create")

        if(bitmap!=null)
            println(bitmap)
            imageView.setImageBitmap(bitmap)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.edit_button, menu)
        return true
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
           fullname = result.data?.getStringExtra("group09.lab1.FULL_NAME").toString()
           age = result.data?.getStringExtra("group09.lab1.AGE").toString().toInt()
           nickname = result.data?.getStringExtra("group09.lab1.NICKNAME").toString()
           email = result.data?.getStringExtra("group09.lab1.EMAIL").toString()
           location = result.data?.getStringExtra("group09.lab1.LOCATION").toString()
           skills = result.data?.getStringExtra("group09.lab1.SKILLS").toString()
           description=result.data?.getStringExtra("group09.lab1.DESCRIPTION").toString()


           fullnameView.text = fullname
           ageView.text = age.toString()
           nicknameView.text = nickname
           emailView.text = email
           locationView.text = location
           skillsView.text = skills
           descriptionView.text = description



           println("Main- result before get lancher ")
           if(bitmap!=null){
               println(bitmap)
           }


           storeInfoSP()
           getProfileImageLFS()

           val iv = findViewById<ImageView>(R.id.imageView)
           if(bitmap!=null){
               iv.setImageBitmap(bitmap)
               println("Main- result after get lancher ")
               println(bitmap)
           }

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
        println("Main-getInfoSP")
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

        getProfileImageLFS()
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

/*    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("bitmap",bitmap)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        bitmap = savedInstanceState.getParcelable("bitmap")
        val iv = findViewById<ImageView>(R.id.imageView)
        if(bitmap!=null)
           iv.setImageBitmap(bitmap)
    }*/

}
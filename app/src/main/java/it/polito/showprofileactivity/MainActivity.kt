package it.polito.showprofileactivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    lateinit var fullname: String
    lateinit var nickname: String
    lateinit var email: String
    lateinit var location: String

    lateinit var sharedPref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var fullnameView = findViewById<TextView>(R.id.fullName)
        var nicknameView = findViewById<TextView>(R.id.nickname)
        var emailView = findViewById<TextView>(R.id.email)
        var locationView = findViewById<TextView>(R.id.location)

        sharedPref = this?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        var jsonDefault = JSONObject()

        jsonDefault.put("fullname", "Mario Rossi");
        jsonDefault.put("nickname", "Mario98");
        jsonDefault.put("email", "mario.rossi@gmail.com");
        jsonDefault.put("location", "Torino");

        var profileString = sharedPref.getString("profile", jsonDefault.toString())

        var json = JSONObject(profileString)

        fullname = json.getString("fullname")
        nickname = json.getString("nickname")
        email = json.getString("email")
        location = json.getString("location")

        fullnameView.text = fullname
        nicknameView.text = nickname
        emailView.text = email
        locationView.text = location
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

                val nicknameView = findViewById<TextView>(R.id.nickname)
                nicknameView.text = result.data?.getStringExtra("group09.lab1.NICKNAME")

                val emailView =  findViewById<TextView>(R.id.email)
                emailView.text = result.data?.getStringExtra("group09.lab1.EMAIL")

                val locationView = findViewById<TextView>(R.id.location)
                locationView.text = result.data?.getStringExtra("group09.lab1.LOCATION")

                fullname = result.data?.getStringExtra("group09.lab1.FULL_NAME").toString()
                nickname = result.data?.getStringExtra("group09.lab1.NICKNAME").toString()
                email = result.data?.getStringExtra("group09.lab1.EMAIL").toString()
                location = result.data?.getStringExtra("group09.lab1.LOCATION").toString()


                var jsonProfile = JSONObject()

                jsonProfile.put("fullname", fullname);
                jsonProfile.put("nickname", nickname);
                jsonProfile.put("email", email);
                jsonProfile.put("location", location);

                val editor = sharedPref.edit()
                editor.putString("profile", jsonProfile.toString())
                editor.apply()
            }
        }

    private fun editProfile(){
        //call edit activity
        val intent = Intent(this, EditProfileActivity::class.java)
        val fullnameView = findViewById<TextView>(R.id.fullName)
        val nicknameView = findViewById<TextView>(R.id.nickname)
        val emailView =  findViewById<TextView>(R.id.email)
        val locationView = findViewById<TextView>(R.id.location)

        intent.putExtra("group09.lab1.FULL_NAME", fullnameView.text)
        intent.putExtra("group09.lab1.NICKNAME", nicknameView.text)
        intent.putExtra("group09.lab1.EMAIL", emailView.text)
        intent.putExtra("group09.lab1.LOCATION", locationView.text)
        resultLauncher.launch(intent)
    }

}
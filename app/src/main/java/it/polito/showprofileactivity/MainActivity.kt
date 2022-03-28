package it.polito.showprofileactivity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts


class MainActivity : AppCompatActivity() {
    var fullname = "Full Name"
    var nickname = "Nickname"
    var email = "email@address"
    var location = "Location"
    lateinit var fullnameView: TextView
    lateinit var nicknameView: TextView
    lateinit var emailView: TextView
    lateinit var locationView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fullnameView = findViewById<TextView>(R.id.fullName)
        nicknameView = findViewById<TextView>(R.id.nickname)
        emailView = findViewById<TextView>(R.id.email)
        locationView = findViewById<TextView>(R.id.location)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("fullname", fullname)
        outState.putString("nickname", nickname)
        outState.putString("email", email)
        outState.putString("location", location)
    }

    //to fetch the data of the previous instance
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        fullname = savedInstanceState.getString("fullname", "Full Name")
        nickname = savedInstanceState.getString("nickname", "Nickname")
        email = savedInstanceState.getString("email", "email@address")
        location = savedInstanceState.getString("location", "Location")
        fullnameView.text = fullname
        nicknameView.text = nickname
        emailView.text = email
        locationView.text = location
   }

}
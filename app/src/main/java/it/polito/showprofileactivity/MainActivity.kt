package it.polito.showprofileactivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView

var fullname = "FullNam"
var nickname = "Nickname"
var email = "email@address.com"
var location = "Location"

class MainActivity : AppCompatActivity() {
    var receiver: BroadcastReceiver? = null


    override fun onResume() {
        super.onResume()
        //infos setting
        var fullname_view = findViewById<TextView>(R.id.fullName)
        fullname_view.setText(fullname)
        var nickname_view = findViewById<TextView>(R.id.Nickname)
        nickname_view.setText(nickname)
        var email_view =  findViewById<TextView>(R.id.Email)
        email_view.setText(email)
        var location_view = findViewById<TextView>(R.id.Location)
        location_view.setText(location)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //infos setting
        var fullname_view = findViewById<TextView>(R.id.fullName)
        fullname_view.setText(fullname)
        var nickname_view = findViewById<TextView>(R.id.Nickname)
        nickname_view.setText(nickname)
        var email_view =  findViewById<TextView>(R.id.Email)
        email_view.setText(email)
        var location_view = findViewById<TextView>(R.id.Location)
        location_view.setText(location)

        //receiving broadcast intents
        val filter = IntentFilter()
        filter.addAction("it.polito.showprofileActivity")
        receiver = MyReceiver()
        registerReceiver(receiver,filter)
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
                //call edit activity
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("fullname",fullname)
                intent.putExtra("nickname",nickname)
                intent.putExtra("email",email)
                intent.putExtra("location",location)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

class MyReceiver : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            if(intent.getStringExtra("fullname") != null)
                fullname = intent.getStringExtra("fullname") ?: fullname
            if(intent.getStringExtra("nickname") != null)
                nickname = intent.getStringExtra("nickname") ?: nickname
            if(intent.getStringExtra("email") != null)
                email = intent.getStringExtra("email") ?: email
            if(intent.getStringExtra("location") != null)
                location = intent.getStringExtra("location") ?: location
        }
    }
}
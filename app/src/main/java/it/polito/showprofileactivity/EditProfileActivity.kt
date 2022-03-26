package it.polito.showprofileactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doOnTextChanged

class EditProfileActivity : AppCompatActivity() {
    var fullname = "FullName"
    var nickname = "Nickname"
    var email = "email@address.com"
    var location = "Location"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        ///get extras
        val i = intent
        fullname = i.getStringExtra("fullname").toString()
        nickname = i.getStringExtra("nickname").toString()
        email = i.getStringExtra("email").toString()
        location = i.getStringExtra("location").toString()

        ///cerco componenti text e setto testo di default
        var fullname_view = findViewById<EditText>(R.id.Edit_FullName)
        fullname_view.setText(fullname)
        var nickname_view = findViewById<EditText>(R.id.Edit_Nickname)
        nickname_view.setText(nickname)
        var email_view =  findViewById<EditText>(R.id.Edit_Email)
        email_view.setText(email)
        var location_view = findViewById<EditText>(R.id.Edit_Location)
        location_view.setText(location)

        //floating context menù
        var image_view = findViewById<ImageView>(R.id.imageView_Edit)
        registerForContextMenu(image_view)

        ///aggiungo listener
        fullname_view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                fullname = s.toString()
                sendNotification("fullname",fullname)
            }
            override fun afterTextChanged(s: Editable) {}
        })

        nickname_view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                nickname = s.toString()
                sendNotification("nickname",nickname)
            }
            override fun afterTextChanged(s: Editable) {}
        })

        email_view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                email = s.toString()
                sendNotification("email",email)
            }
            override fun afterTextChanged(s: Editable) {}
        })

        location_view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                location = s.toString()
                sendNotification("location",location)
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun sendNotification(info:String, data:String){
        val intent = Intent()
        intent.action = "it.polito.showprofileActivity"
        intent.putExtra(info,data)
        intent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        sendBroadcast(intent)
        //Log.i(info,data)
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo){
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.context_menu, menu)
    }
}




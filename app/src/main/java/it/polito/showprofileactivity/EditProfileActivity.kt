package it.polito.showprofileactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.core.widget.doOnTextChanged

class EditProfileActivity : AppCompatActivity() {
    var fullname = "FullNam"
    var nickname = "Nickname"
    var email = "email@address.com"
    var location = "Location"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        ///cerco componenti text e setto testo di default
        var fullname_view = findViewById<EditText>(R.id.Edit_FullName)
        fullname_view.setText(fullname)
        var nickname_view = findViewById<EditText>(R.id.Edit_Nickname)
        nickname_view.setText(nickname)
        var email_view =  findViewById<EditText>(R.id.Edit_Email)
        email_view.setText(email)
        var location_view = findViewById<EditText>(R.id.Edit_Location)
        location_view.setText(location)

        ///aggiungo listener
        fullname_view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                fullname = s.toString()
            }
            override fun afterTextChanged(s: Editable) {}
        })

        nickname_view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                nickname = s.toString()
            }
            override fun afterTextChanged(s: Editable) {}
        })

        email_view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                email = s.toString()
            }
            override fun afterTextChanged(s: Editable) {}
        })

        location_view.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                location = s.toString()
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun sendNotification(info:String){
        val intent = Intent()
        intent.action = ""
    }
}
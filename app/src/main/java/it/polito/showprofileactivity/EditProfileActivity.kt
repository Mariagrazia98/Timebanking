package it.polito.showprofileactivity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        ///get extras
        val i = intent
        val fullname = i.getStringExtra("group09.lab1.FULL_NAME")
        val nickname = i.getStringExtra("group09.lab1.NICKNAME")
        val email = i.getStringExtra("group09.lab1.EMAIL")
        val location = i.getStringExtra("group09.lab1.LOCATION")

        val fullnameView = findViewById<EditText>(R.id.Edit_FullName)
        fullnameView.setText(fullname)
        val nicknameView = findViewById<EditText>(R.id.Edit_Nickname)
        nicknameView.setText(nickname)
        val emailView =  findViewById<EditText>(R.id.Edit_Email)
        emailView.setText(email)
        val locationView = findViewById<EditText>(R.id.Edit_Location)
        locationView.setText(location)

        val imageButton = findViewById<Button>(R.id.imageButton)
        registerForContextMenu(imageButton)
    }

    override fun onBackPressed() {
        val i = intent
        val fullname = findViewById<EditText>(R.id.Edit_FullName).text.toString()
        val nickname = findViewById<EditText>(R.id.Edit_Nickname).text.toString()
        val email =  findViewById<EditText>(R.id.Edit_Email).text.toString()
        val location = findViewById<EditText>(R.id.Edit_Location).text.toString()

        i.putExtra("group09.lab1.FULL_NAME", fullname)
        i.putExtra("group09.lab1.NICKNAME", nickname)
        i.putExtra("group09.lab1.EMAIL", email)
        i.putExtra("group09.lab1.LOCATION", location)
        setResult(Activity.RESULT_OK, i)
        super.onBackPressed() // to call at the end, because it calls internally the finish() method
    }


    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.image_menu, menu)
    }

}
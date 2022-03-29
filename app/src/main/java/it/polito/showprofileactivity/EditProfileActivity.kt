package it.polito.showprofileactivity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuInflater
import android.view.View
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts

class EditProfileActivity : AppCompatActivity() {
    private lateinit var iv: ImageView

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

        val imgButton = findViewById<Button>(R.id.imageButton)
        registerForContextMenu(imgButton)

        iv = findViewById(R.id.Edit_imageView)
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

    //create the floating menu after pressing on the camera img
    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.image_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.camera_item -> {
                openCamera()
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
        val bitmap = intent?.extras?.get("data") as Bitmap
        iv.setImageBitmap(bitmap)

    }
}
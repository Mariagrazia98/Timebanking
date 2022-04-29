package it.polito.timebanking

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf

import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.navigation.NavigationView
import it.polito.timebanking.databinding.ActivityMainBinding
import it.polito.timebanking.repository.User
import it.polito.timebanking.viewmodel.ProfileViewModel
import it.polito.timebanking.viewmodel.TimeSlotViewModel
import java.io.File

class MainActivity : AppCompatActivity() {
    private val profileViewModel:ProfileViewModel by viewModels()
    private val slotViewModel:TimeSlotViewModel by viewModels()
    private var userId:Long=0
    lateinit private var navController: NavController
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    private lateinit var binding: ActivityMainBinding

    lateinit var user: User

    fun getProfileImagePath(): String{
        //Get profile image from internal storage (local filesystem)
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "profileImage.jpg")
        return file.absolutePath
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //drawer settings
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        binding = ActivityMainBinding.inflate(layoutInflater)
        setupWithNavController(binding.navigationView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        profileViewModel.clearUsers()
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationView)
        profileViewModel.getAllUsers()?.observe(this) {
            //calculate actual number of users (workaround to infinite loop)
            if(it.isEmpty()){
                println("there is no user?")
                user = User()
                user.fullname = "Mario Rossi"
                user.nickname = "Mario98"
                user.email = "mario.rossi@gmail.com"
                user.location = "Torino"
                user.description="Student"
                user.skills="Android developer"
                user.age=24
                user.imagePath = getProfileImagePath()
                profileViewModel.addUser(user)
            }else{
                userId = it[0].id
                //aggiornamento
                findViewById<TextView>(R.id.titleHeader).text = it[0].nickname
                findViewById<TextView>(R.id.subtitleHeader).text = it[0].email
                findViewById<ImageView>(R.id.imageViewHeader).setImageBitmap(BitmapFactory.decodeFile(it[0].imagePath))
            }
        }

        navView.setNavigationItemSelectedListener(){
            item ->
            if (item.itemId == R.id.profileMenuItem){
                // your code
                navController.navigate(R.id.showProfileFragment)
            }
            else if (item.itemId ==R.id.advMenuItem){
                // your code
                navController.navigate(R.id.timeSlotListFragment)
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true;

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.edit_button, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var bundle = bundleOf("id" to userId)
        return when (item.itemId) {
            R.id.edit_button -> {
                navController.navigate(R.id.editProfileFragment, bundle)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

}

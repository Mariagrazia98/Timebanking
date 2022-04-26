package it.polito.timebanking

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import it.polito.timebanking.databinding.ActivityMainBinding
import it.polito.timebanking.repository.User
import it.polito.timebanking.viewmodel.ProfileViewModel
import it.polito.timebanking.viewmodel.TimeSlotViewModel

class MainActivity : AppCompatActivity() {
    private val profileViewModel:ProfileViewModel by viewModels()
    private val slotViewModel:TimeSlotViewModel by viewModels()

    lateinit private var navController: NavController
    lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding

    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //drawer settings
        drawerLayout = findViewById(R.id.drawerLayout)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding = ActivityMainBinding.inflate(layoutInflater)
        setupWithNavController(binding.navigationView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)

        Log.d("DEBUG","onc")
        user = User()
        user.fullname = "Luca Neri"
        user.nickname = "Luca98"
        user.email = "luca.neri@gmail.com"
        user.location = "Torino"
        user.description="Student"
        user.skills="Android developer"
        user.age=18

       profileViewModel.addUser(user)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

}

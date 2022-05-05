package it.polito.timebanking

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaParser.SeekPoint.START
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import it.polito.timebanking.databinding.ActivityMainBinding
import it.polito.timebanking.model.UserFire
import it.polito.timebanking.repository.User
import it.polito.timebanking.viewmodel.ProfileViewModel
import java.io.File


class MainActivity : AppCompatActivity() {
    private val profileViewModel: ProfileViewModel by viewModels()
    var userId: Long = 0
    lateinit var navController: NavController
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    private lateinit var binding: ActivityMainBinding

    lateinit var user: User

    //Firebase
    lateinit var mAuth: FirebaseAuth
    private var userState: FirebaseUser? = null

    // Choose authentication providers
    val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

    // See: https://developer.android.com/training/basics/intents/result
    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
            this.onSignInResult(res)
        }


    fun getProfileImage(): File {
        //Get profile image from internal storage (local filesystem)
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        return File(file, "profileImage.jpg")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //drawer initialize
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        binding = ActivityMainBinding.inflate(layoutInflater)
        setupWithNavController(binding.navigationView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationView)


        //Drawer item
        var log_item = navView.menu.findItem(R.id.nav_log)
        var profile_item = navView.menu.findItem(R.id.profileMenuItem)
        var adv_item = navView.menu.findItem(R.id.advMenuItem)


        //initialize the FirebaseAuth instance.
        mAuth = FirebaseAuth.getInstance();

        //Listener called when there is a change in the authentication state
        mAuth.addAuthStateListener { authState ->
            Log.d("AuthListener", "Inside add AuthStateListener ")
            userState = authState.currentUser
            userState?.reload()
            if (userState == null) {
                Log.d("AuthListener", "null user")

                log_item.title = "Login"
                log_item.setOnMenuItemClickListener {
                    login()
                    true
                }

                profile_item.isVisible = false
                adv_item.isVisible = false


            } else {
                Log.d("AuthListener", "there is a user")
                log_item.title = "Logout"
                log_item.setOnMenuItemClickListener {
                    logout()
                    true
                }

                profile_item.isVisible = true
                adv_item.isVisible = true


                /* findViewById<TextView>(R.id.titleHeader).text = userState!!.displayName
                 findViewById<TextView>(R.id.subtitleHeader).text = userState!!.email
                 println(userState!!.photoUrl.toString())
                 findViewById<ImageView>(R.id.imageViewHeader).setImageBitmap(
                     BitmapFactory.decodeFile(
                         userState!!.photoUrl.toString()
                     )
                 )*/

            }

        }


        /*  profileViewModel.getAllUsers()?.observe(this) {
              if (it.isEmpty()) { //if there is no user in the db, create a new one with the information below
                  user = User()
                  user.fullname = "Mario Rossi"
                  user.nickname = "Mario98"
                  user.email = "mario.rossi@gmail.com"
                  user.location = "Torino"
                  user.description = "Student"
                  user.skills = "Android developer"
                  user.age = 24
                  user.imagePath = getProfileImage().absolutePath
                  profileViewModel.addUser(user)
              } else { //for this lab we just considered the existence of one single user, so if there is at least one user we take the first one
                  userId = it[0].id
                  //update drawer
                  /*findViewById<TextView>(R.id.titleHeader).text = it[0].nickname
                  findViewById<TextView>(R.id.subtitleHeader).text = it[0].email
                  if(getProfileImage().exists()){
                      findViewById<ImageView>(R.id.imageViewHeader).setImageBitmap(
                          BitmapFactory.decodeFile(
                              it[0].imagePath
                          )
                      )
                  }*/

              }
          }
      */

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.editProfileFragment || destination.id == R.id.timeSlotEditFragment) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.setDisplayShowHomeEnabled(false)
            }
        }

        navView.setNavigationItemSelectedListener() { item ->
            if (item.itemId == R.id.profileMenuItem) {
                navController.navigate(R.id.showProfileFragment)
            } else if (item.itemId == R.id.advMenuItem) {
                navController.navigate(R.id.timeSlotListFragment)
            } else if (item.itemId == R.id.nav_log) {
                login()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun login() {
        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()


        signInLauncher.launch(signInIntent)
    }

    private fun logout() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnSuccessListener {
                Log.d("LOGIN", "Logout successful!")
                Toast.makeText(this, "Logout successful!", Toast.LENGTH_SHORT).show()
                findViewById<TextView>(R.id.titleHeader).text = ""
                findViewById<TextView>(R.id.subtitleHeader).text = ""
            }
    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            // ...
            user?.reload()
            user?.getIdToken(true)

            if (user != null) {


                profileViewModel.getUserByIdF(user.uid)
                    .observe(this,
                        Observer
                        { document ->
                            if (document != null) {
                                Log.d("LOGIN", user.uid.toString())

                                // timestamp of latest login -> it triggers the observer and loads the user data
                                val updates = hashMapOf<String, Any>(
                                    "timestamp" to FieldValue.serverTimestamp()
                                )

                                profileViewModel.loginUser(user.uid, updates)
                                    .observe(this, Observer { isSuccess ->
                                        if (isSuccess) {
                                            startActivity(Intent(this, MainActivity::class.java))
                                            overridePendingTransition(0, 0)
                                            finish()
                                            overridePendingTransition(0, 0)
                                            Toast.makeText(
                                                this,
                                                "Welcome back!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    })

                            } else {
                                Log.d("LOGIN", "New user signed up")
                                val newUser = UserFire(
                                    uid = user.uid,
                                    fullname = if (user.displayName != null) user.displayName.toString() else "",
                                    email = user.email!!,
                                    imagePath = if (user.photoUrl != null) user.photoUrl!!.toString() else null
                                )
                                profileViewModel.addUserF(newUser)
                                    .observe(this, Observer { isSuccess ->
                                        if (isSuccess) {
                                            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    })
                            }

                            Log.d("Login result", "Sign in success")
                            // ...
                        })
            }
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Log.e("Login result", "Sign in failed")
        }
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.edit_button, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val bundle = bundleOf("id" to (userState?.uid ?: 0))
        return when (item.itemId) {
            R.id.edit_button -> {
                navController.navigate(R.id.editProfileFragment, bundle)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        return NavigationUI.navigateUp(navController, drawerLayout)
    }


}

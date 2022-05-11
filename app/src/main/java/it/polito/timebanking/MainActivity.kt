package it.polito.timebanking


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
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
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import de.hdodenhof.circleimageview.CircleImageView
import it.polito.timebanking.databinding.ActivityMainBinding
import it.polito.timebanking.model.UserFire
import it.polito.timebanking.repository.User
import it.polito.timebanking.viewmodel.ProfileViewModel
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val profileViewModel: ProfileViewModel by viewModels()

    //var userId: Long = 0
    lateinit var navController: NavController
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    private lateinit var binding: ActivityMainBinding
    private lateinit var headerView:View
    private lateinit var navTitle:TextView
    private lateinit var navSubtitle:TextView
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //drawer initialize
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        binding = ActivityMainBinding.inflate(layoutInflater)
        setupWithNavController(binding.navigationView, navController)

        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navigationView)
        headerView = navView.getHeaderView(0)
        navTitle = headerView.findViewById(R.id.titleHeader)
        navSubtitle = headerView.findViewById(R.id.subtitleHeader)


        //Drawer items
        val log_item = navView.menu.findItem(R.id.nav_log)
        val profile_item = navView.menu.findItem(R.id.profileMenuItem)
        val adv_item = navView.menu.findItem(R.id.advMenuItem)


        //initialize the FirebaseAuth instance.
        mAuth = FirebaseAuth.getInstance()

        //Listener called when there is a change in the authentication state
        mAuth.addAuthStateListener { authState ->
            Log.d("AuthListener", "Inside add AuthStateListener ")
            userState = authState.currentUser
            userState?.reload()
            if (userState == null) {
                log_item.title = "Login"
                log_item.setOnMenuItemClickListener {
                    login()
                    true
                }

                profile_item.isVisible = false
                adv_item.isVisible = false

            } else {

                profileViewModel.getUserByIdF(userState!!.uid)
                    .observe(this, Observer { user ->
                        if (user != null) {
                            navTitle.text= user.fullname
                            navSubtitle.text=user.email
                            //todo: aggiungere verifica file esiste
                            // Download directly from StorageReference using Glide
                            Glide.with(this /* context */).load(user.imagePath).into( headerView.findViewById<CircleImageView>(R.id.imageViewHeader))

                        }
                    })

                log_item.title = "Logout"
                log_item.setIcon(R.drawable.ic_logout)
                log_item.setOnMenuItemClickListener {
                    logout()
                    true
                }

                profile_item.isVisible = true
                adv_item.isVisible = true

            }

        }


        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.editProfileFragment || destination.id == R.id.timeSlotEditFragment) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.setDisplayShowHomeEnabled(false)
            }
        }


        navView.setNavigationItemSelectedListener() { item ->
            val bundle = bundleOf("userId" to (userState?.uid))

            if (item.itemId == R.id.profileMenuItem) {
                navController.navigate(R.id.showProfileFragment, bundle)
            } else if (item.itemId == R.id.advMenuItem) {
                navController.navigate(R.id.timeSlotListFragment, bundle)
            } else if (item.itemId == R.id.skillsMenuItem) {
                navController.navigate(R.id.skillsListFragment, bundle)
            }else if (item.itemId == R.id.nav_log) {
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
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            user?.reload()
            user?.getIdToken(true)

            if (user != null) {
                profileViewModel.getUserByIdF(user.uid)
                    .observe(this, Observer { document ->
                        if (document != null) {
                            Log.d("LOGIN", user.uid)

                            // timestamp of latest login
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
                                            "Welcome back " + user.displayName + "!",
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
                                        Toast.makeText(
                                            this,
                                            "Welcome!" + user.displayName + "!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        }
                    })
            }
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Log.e("Login result", "Sign in failed")
            Toast.makeText(this, "Sign in failed, try later..", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.edit_button, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val bundle = bundleOf("userId" to (userState?.uid ?: 0))
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

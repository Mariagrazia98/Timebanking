package it.polito.timebanking


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
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
import it.polito.timebanking.model.User
import it.polito.timebanking.viewmodel.ProfileViewModel

class MainActivity : AppCompatActivity() {
    private val profileViewModel: ProfileViewModel by viewModels()

    lateinit var navController: NavController
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    private lateinit var binding: ActivityMainBinding
    lateinit var headerView:View
    private lateinit var navTitle:TextView
    private lateinit var navSubtitle:TextView
    var adapterTimeSlots : MyTimeSlotRecyclerViewAdapter? = null
    var keepAdapter : Boolean = false //for filter tool

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


        //initialize the FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance()

        //Listener called when there is a change in the authentication state
        mAuth.addAuthStateListener { authState ->
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

                profileViewModel.getUserById(userState!!.uid)
                    .observe(this, Observer { user ->
                        if (user != null) {
                            navTitle.text= user.fullname
                            navSubtitle.text=user.email
                            if(user.imagePath!=null){
                                var imageHeader=  headerView.findViewById<CircleImageView>(R.id.imageViewHeader)
                                Glide.with(this /* context */).load(user.imagePath).into(imageHeader)
                            }
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
                Toast.makeText(this, "Logout successful!", Toast.LENGTH_SHORT).show()
                findViewById<TextView>(R.id.titleHeader).text = ""
                findViewById<TextView>(R.id.subtitleHeader).text = ""
                headerView.findViewById<CircleImageView>(R.id.imageViewHeader).setImageDrawable(null)
            }
    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            user?.reload()
            user?.getIdToken(true)

            if (user != null) {
                profileViewModel.getUserById(user.uid)
                    .observe(this, Observer { document ->
                        if (document != null) {

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
                            val newUser = User(
                                uid = user.uid,
                                fullname = if (user.displayName != null) user.displayName.toString() else "",
                                email = user.email!!,
                                imagePath = if (user.photoUrl != null) user.photoUrl!!.toString() else null
                            )
                            profileViewModel.addUser(newUser)
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
            // Sign in failed
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

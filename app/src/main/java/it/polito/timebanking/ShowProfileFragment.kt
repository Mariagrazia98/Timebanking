package it.polito.timebanking

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import org.json.JSONObject
import java.io.File

class ShowProfileFragment : Fragment() {
    lateinit var fullname: String
    lateinit var nickname: String
    var age = 24
    lateinit var email: String
    lateinit var location: String
    lateinit var skills: String
    lateinit var description: String
    private var bitmap: Bitmap? = null
    lateinit var sharedPref : SharedPreferences

    lateinit var fullnameView: TextView
    lateinit var ageView: TextView
    lateinit var nicknameView: TextView
    lateinit var emailView: TextView
    lateinit var locationView: TextView
    lateinit var skillsView: TextView
    lateinit var descriptionView: TextView
    lateinit var imageView : ImageView
    lateinit var frameLayout : FrameLayout
    var h: Int = 0
    var w: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_profile, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.edit_button, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item!!, requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val sv = view.findViewById<ScrollView>(R.id.scrollView)
            frameLayout = view.findViewById(R.id.frameLayout)

            sv.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    h = sv.height
                    w = sv.width

                    frameLayout.post{frameLayout.layoutParams = LinearLayout.LayoutParams(w-2*convertDpToPixel(16).toInt(), h/3)}
                    sv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }

        fullnameView = view.findViewById(R.id.fullName)
        ageView = view.findViewById(R.id.age)
        nicknameView = view.findViewById(R.id.nickname)
        emailView = view.findViewById(R.id.email)
        locationView = view.findViewById(R.id.location)
        skillsView = view.findViewById(R.id.skills)
        descriptionView = view.findViewById(R.id.description)
        imageView = view.findViewById(R.id.imageView)

        getInfoSP()
        getProfileImageLFS()

        if(bitmap!=null)
            imageView.setImageBitmap(bitmap)
    }








    fun convertDpToPixel(dp: Int): Float {
        return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            frameLayout.post{frameLayout.layoutParams = LinearLayout.LayoutParams(w - 2*convertDpToPixel(16).toInt(), h/3)}
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            frameLayout.post{frameLayout.layoutParams = LinearLayout.LayoutParams(w/3, h)}
        }
    }

    fun storeInfoSP(){
        //Save data to SharedPreferences in a JSON object
        val jsonProfile = JSONObject()
        jsonProfile.put("fullname", fullname)
        jsonProfile.put("age", age)
        jsonProfile.put("nickname", nickname)
        jsonProfile.put("email", email)
        jsonProfile.put("location", location)
        jsonProfile.put("skills", skills)
        jsonProfile.put("description", description)

        val editor = sharedPref.edit()
        editor.putString("profile", jsonProfile.toString()) //sharedPref saves (key,value) pair and this method wants a string as value
        editor.apply()
    }

    fun getInfoSP(){
        //Get data from SharedPreferences
        sharedPref = requireActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        //Create a json with default data to be used if sharedPref doesn't contain anything
        val jsonDefault = JSONObject()
        jsonDefault.put("fullname", "Mario Rossi")
        jsonDefault.put("age", 24)
        jsonDefault.put("nickname", "Mario98")
        jsonDefault.put("email", "mario.rossi@gmail.com")
        jsonDefault.put("location", "Torino")
        jsonDefault.put("skills", "Android developer, Electrician")
        jsonDefault.put("description", "Frequento il Politecnico di Torino")

        val profileString = sharedPref.getString("profile", jsonDefault.toString()) //retrieve the string containing data in json format with the key "profile"
        val json = JSONObject(profileString.toString())  //transform the obtained string into a json to easily access all the fields

        fullname = if(json.has("fullname")) json.getString("fullname") else jsonDefault.getString("fullname")
        age = if(json.has("age")) json.getInt("age") else jsonDefault.getInt("age")
        nickname = if(json.has("nickname")) json.getString("nickname") else jsonDefault.getString("nickname")
        email = if(json.has("email")) json.getString("email") else jsonDefault.getString("email")
        location = if(json.has("location")) json.getString("location") else jsonDefault.getString("location")
        skills = if(json.has("skills")) json.getString("skills") else jsonDefault.getString("skills")
        description = if(json.has("description")) json.getString("description") else jsonDefault.getString("description")

        fullnameView.text = fullname
        ageView.text = age.toString()
        nicknameView.text = nickname
        emailView.text = email
        locationView.text = location
        skillsView.text = skills
        descriptionView.text=description
    }

    fun getProfileImageLFS(){
        //Get profile image from internal storage (local filesystem)
        val wrapper = ContextWrapper(requireActivity().applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "profileImage.jpg")
        bitmap = BitmapFactory.decodeFile(file.absolutePath)
    }
}
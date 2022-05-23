

package it.polito.timebanking

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import it.polito.timebanking.MainActivity


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Handler().postDelayed(Runnable { //This method will be executed once the timer is over
            // Start your app main activity
            val i = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(i)
            // close this activity
            finish()
        }, 3000)
    }
}
package mrigapps.android.test_project.Inteface

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import mrigapps.android.test_project.R


class SplashScreen : AppCompatActivity() {

    private val delayMillis: Long = 1000
    private val fadeInDuration: Long = 1000
    private val appNameVisibleDuration: Long = 1000

    private lateinit var appNameText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        supportActionBar?.hide()

        appNameText = findViewById(R.id.appnamescreen)
        appNameText.visibility = View.VISIBLE // Initially hide the app name


        // Set the application name text
        val appName = getString(R.string.app_name)


        val spannableString = SpannableString(appName)

        // Set the color spans for different parts of the text
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#1FB6F6")),
            0, 6, // Change the indices according to your desired color combination
            0
        )
        spannableString.setSpan(
            ForegroundColorSpan(Color.parseColor("#87E8B6")),
            6, appName.length,
            0
        )

        // Set the SpannableString to the TextView
        appNameText.text = spannableString

        // Create the fade-in animation
        val fadeInAnimation = AlphaAnimation(0f, 1f).apply {
            duration = fadeInDuration
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    // Empty implementation
                }

                override fun onAnimationEnd(animation: Animation?) {
                    // Delay making the app name invisible
                    Handler(Looper.getMainLooper()).postDelayed({
                        appNameText.visibility = View.VISIBLE
                    }, appNameVisibleDuration)
                }

                override fun onAnimationRepeat(animation: Animation?) {
                    // Empty implementation
                }
            })
        }

        // Apply the animation to the TextView
        appNameText.startAnimation(fadeInAnimation)

        // Start the LoginActivity after the delay
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreen, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, delayMillis + fadeInDuration + appNameVisibleDuration)
    }


}
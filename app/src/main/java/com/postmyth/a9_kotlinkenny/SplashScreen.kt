package com.postmyth.a9_kotlinkenny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this,R.color.black)

        val handler = Handler()
        handler.postDelayed({

            if (Firebase.auth.currentUser != null) {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, LoginPage::class.java)
                startActivity(intent)
                finish()
            }

        },2000)
    }
}

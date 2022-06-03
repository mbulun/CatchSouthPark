package com.postmyth.a9_kotlinkenny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.postmyth.a9_kotlinkenny.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

var degisken = ""
var zamanDegisken = "secilmedi"
var toplamZaman = 0
var statusTime = false
var statusChar = false
private lateinit var auth : FirebaseAuth
private lateinit var binding : ActivityMainBinding
private lateinit var firebaseAnalytics: FirebaseAnalytics

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        statusTime = false
        statusChar = false
        auth = Firebase.auth
        db = Firebase.firestore
        firebaseAnalytics = Firebase.analytics
        supportActionBar?.hide()
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white))

        println(auth.currentUser!!.email)

        val reklamtalebi = AdRequest.Builder().build()
        adView.loadAd(reklamtalebi)

        }

    override fun onResume() {
        super.onResume()
        db.collection("Haftalık Ödül Puan").addSnapshotListener { value, error ->
            if (value != null) {
                for (doc in value!!.documents) {
                    val point = doc.get("Point")
                    val title = doc.get("Title")
                    //println("" + point + "  " + title)
                    binding.usernameText.text = "" + title + " " + point
                }
            } else println("value null geliyor")
        }

        if (!networkControl.isNetworkAvailable(this)) {
            println("hereeeeeeeeeeee")
            Toast.makeText(this@MainActivity,
                "Lütfen İnternetinizi Açın, Skorunuzun kaydedilmesi ve para kazanabilmeniz için internet gereklidir. " +
                        "Uygulamayı kapatıp internet açıktan sonra tekrar giriş yapınız.",Toast.LENGTH_LONG).show()
            val intent = Intent(this,SplashScreen::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun basla(view: View) {

        if (statusChar && statusTime) {
            val oyunaBasla = Intent(applicationContext, GameScreen::class.java)
            startActivity(oyunaBasla)
        } else if (statusTime) {
            Toast.makeText(
                this@MainActivity,
                "Hey ! Karakterini Seçmeyi Unuttun",
                Toast.LENGTH_LONG
            ).show()
        } else if (statusChar) {
            Toast.makeText(
                this@MainActivity,
                "Hey ! Süreyi Seçmeyi Unuttun",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                this@MainActivity,
                "Hey ! Karakterini ve Süreyi Seçmeyi Unuttun",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    fun scaleImage() {
        token.scaleX = 1F
        token.scaleY = 1F
        kyle.scaleX = 1F
        kyle.scaleY = 1F
        kenny.scaleX = 1F
        kenny.scaleY = 1F
        eric.scaleX = 1F
        eric.scaleY = 1F
        stan.scaleX = 1F
        stan.scaleY = 1F
        tweek.scaleX = 1F
        tweek.scaleY = 1F
    }
    fun token(view: View) {
        degisken = "token"
        statusChar = true
        scaleImage()
        token.scaleX = 1.6F
        token.scaleY = 1.6F
    }
    fun kyle(view: View) {
        degisken = "kyle"
        statusChar = true
        scaleImage()
        kyle.scaleX = 1.6F
        kyle.scaleY = 1.6F
    }
    fun kenny(view: View) {
        degisken = "kenny"
        statusChar = true
        scaleImage()
        kenny.scaleX = 1.6F
        kenny.scaleY = 1.6F
    }
    fun eric(view: View) {
        degisken = "eric"
        statusChar = true
        scaleImage()
        eric.scaleX = 1.6F
        eric.scaleY = 1.6F
    }
    fun stan(view: View) {
        degisken = "stan"
        statusChar = true
        scaleImage()
        stan.scaleX = 1.6F
        stan.scaleY = 1.6F
    }
    fun tweek(view: View) {
        degisken = "tweek"
        statusChar = true
        scaleImage()
        tweek.scaleX = 1.6F
        tweek.scaleY = 1.6F
    }

    fun scaleButton() {
        button.scaleX = 1F
        button.scaleY = 1F
        button2.scaleX = 1F
        button2.scaleY = 1F
        button3.scaleX = 1F
        button3.scaleY = 1F
    }
    fun shortt(view: View) {
        statusTime = true
        toplamZaman = 12300
        scaleButton()
        button.scaleX = 1.3F
        button.scaleY = 1.3F
        zamanDegisken = "Scores30sec"
    }
    fun midd(view: View) {
        statusTime = true
        toplamZaman = 60300
        scaleButton()
        button2.scaleX = 1.3F
        button2.scaleY = 1.3F
        zamanDegisken = "Scores60sec"
    }
    fun longg(view: View) {
        statusTime = true
        toplamZaman = 120300
        scaleButton()
        button3.scaleX = 1.3F
        button3.scaleY = 1.3F
        zamanDegisken = "Scores120sec"
    }

    fun logout(view: View) {
        println(auth.currentUser!!.email.toString())
        auth.signOut()
        val intent = Intent(this@MainActivity, LoginPage::class.java)
        startActivity(intent)
        finish()
    }

    fun highScoreImage (view: View) {
            val intent = Intent(this@MainActivity,ScoresList::class.java)
            startActivity(intent)
    }

}




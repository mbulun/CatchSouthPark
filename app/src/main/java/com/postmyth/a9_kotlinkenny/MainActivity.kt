package com.postmyth.a9_kotlinkenny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.postmyth.a9_kotlinkenny.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

var degisken = ""
var zamanDegisken = "secilmedi"
var toplamZaman = 0
var statusTime = false
var statusChar = false
private lateinit var auth : FirebaseAuth
private lateinit var binding : ActivityMainBinding
private lateinit var firebaseAnalytics: FirebaseAnalytics
var mInterstitialAd: InterstitialAd? = null


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
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)

        val reklamtalebi = AdRequest.Builder().build()
        adView.loadAd(reklamtalebi)

        loadAd()

        }

    override fun onResume() {
        super.onResume()

        if (!networkControl.isNetworkAvailable(this)) {
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

            if(mInterstitialAd != null) {
                mInterstitialAd?.show(this)
                println("REKLAM IS HERE")
            } else {
                println("The interstitial ad wasn't ready yet.")
            }

            mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    //reklam kapatılınca olacak şey
                    println("Ad was dismissed.")
                    loadAd()

                    val oyunaBasla = Intent(applicationContext, GameScreen::class.java)
                    startActivity(oyunaBasla)
                }

                override fun onAdShowedFullScreenContent() {
                    //Reklam gösterilirken olacak şey
                    println("Ad showed fullscreen content")
                    mInterstitialAd = null
                }
            }

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

    private fun scaleImage() {
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

    private fun scaleButton() {
        button.scaleX = 1F
        button.scaleY = 1F
        button2.scaleX = 1F
        button2.scaleY = 1F
        button3.scaleX = 1F
        button3.scaleY = 1F
    }
    fun shortt(view: View) {
        statusTime = true
        toplamZaman = 30300
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
        auth.signOut()
        val intent = Intent(this@MainActivity, LoginPage::class.java)
        startActivity(intent)
        finish()
    }
    fun highScoreImage (view: View) {

        if(mInterstitialAd != null) {
            mInterstitialAd?.show(this)
            println("REKLAM IS HERE")
        } else {
            println("The interstitial ad wasn't ready yet.")
        }

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                //reklam kapatılınca olacak şey
                println("Ad was dismissed.")
                loadAd()

                val intent = Intent(this@MainActivity,ScoresList::class.java)
                startActivity(intent)
            }

            override fun onAdShowedFullScreenContent() {
                //Reklam gösterilirken olacak şey
                println("Ad showed fullscreen content")
                mInterstitialAd = null
            }
        }

    }

    fun loadAd () {
        var adRequest = AdRequest.Builder().build()

        println("adLoad is called")

        InterstitialAd.load(this,"ca-app-pub-8944524190558053/6548376065", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                println(adError?.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                println("Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })
    }
}




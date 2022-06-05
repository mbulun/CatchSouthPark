package com.postmyth.a9_kotlinkenny

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_game_screen.*

lateinit var countDownTimer : CountDownTimer
private lateinit var auth : FirebaseAuth
var scoreMap = hashMapOf<String,Any>()
var highest30 = 0
var highest60 = 0
var highest120 = 0


class GameScreen : AppCompatActivity() {

    private lateinit var db : FirebaseFirestore
    private var score = 0
    var runnable = Runnable { }
    var handler = Handler(Looper.getMainLooper())
    private var speed = 800
    var gameOver = false
    private var ceza = false
    var timeLeft = 0
    private var enBasla = 0
    private var enSon = 0
    private var boyBasla = 0
    private var boySon = 0
    var katsayi = 10

    private var mRewardedAd: RewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //println("mobile phone test")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_screen)
        supportActionBar?.hide()

        auth = Firebase.auth
        db = Firebase.firestore
        kalanZaman(toplamZaman)
        character()
        loadAd ()

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        enBasla = (width * (0.05)).toInt() + 100
        enSon = (width * (0.95)).toInt() - imageView.height - 150
        boyBasla = (height * (0.15)).toInt() + 100
        boySon = (height * (0.85)).toInt() - imageView.width - 150
        characterLocation()

        when (zamanDegisken) {
            "Scores30sec" -> get_db_30()
            "Scores60sec" -> get_db_60()
            "Scores120sec" -> get_db_120()
        }

        if (!networkControl.isNetworkAvailable(this)) {
            Toast.makeText(this@GameScreen,
                "Lütfen İnternetinizi Açın, Skorunuzun kaydedilmesi ve para kazanabilmeniz için internet gereklidir. " +
                        "Uygulamayı kapatıp internet açıktan sonra tekrar giriş yapınız.",Toast.LENGTH_LONG).show()
            val intent = Intent(this,SplashScreen::class.java)
            startActivity(intent)
            finish()
        }

        val reklamtalebi = AdRequest.Builder().build()
        adView2.loadAd(reklamtalebi)

    }
    override fun onPause() {
        handler.removeCallbacks(runnable)
        countDownTimer.cancel()
        super.onPause()
    }
    override fun onStop() {
        handler.removeCallbacks(runnable)
        countDownTimer.cancel()
        super.onStop()
        val username = auth.currentUser!!.email

        when (zamanDegisken) {
            "Scores30sec" -> {
                scoreMap["score"] = highest30
                scoreMap["userName"] = username!!.substringBefore('@')
                db.collection("Scores30sec").document(username).set(scoreMap)
            }
            "Scores60sec" -> {
                scoreMap["score"] = highest60
                scoreMap["userName"] = username!!.substringBefore('@')
                db.collection("Scores60sec").document(username).set(scoreMap)
            }
            "Scores120sec" -> {
                scoreMap["score"] = highest120
                scoreMap["userName"] = username!!.substringBefore('@')
                db.collection("Scores120sec").document(username).set(scoreMap)
            }
        }
    }
    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        countDownTimer.cancel()
        super.onDestroy()
    }
    override fun onRestart() {
        super.onRestart()
        handler.post(runnable)
        countDownTimer.start()
    }

    @SuppressLint("SetTextI18n")
    fun game(view: View) {

        if (!gameOver) {
            if (!ceza) {
                score++
                textView.text = "Skorun $score"

                if (zamanDegisken == "Scores30sec" && score >= highest30) {
                    highest30 = score
                    textViewHigh.text = "En Yüksek Skorun $highest30"
                }
                else if (zamanDegisken == "Scores60sec" && score >= highest60) {
                    highest60 = score
                    textViewHigh.text = "En Yüksek Skorun $highest60"
                }
                else if (zamanDegisken == "Scores120sec" && score >= highest120) {
                    highest120 = score
                    textViewHigh.text = "En Yüksek Skorun $highest120"
                }

                if (speed > 600) speed -= score * 2
                else if (speed in 300..600) speed -= 10
            }
            else {
                //println("game fun: cezaya dokundun")
                handler.removeCallbacks(runnable)
                countDownTimer.cancel()
                val alert = AlertDialog.Builder(this)
                alert.setMessage("Kırmızıyken Dokunmaman Gerekiyordu   :( Neyse Ki Bir Reklam İle Kaldığın Yerden Devam Edebilirsin" +
                        "(Reklam gösterimi için Wİ-Fİ bağlantısı gerekebilir)")
                alert.setPositiveButton("Hemen İzle") {dialog, which ->

                    mRewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                        override fun onAdShowedFullScreenContent() {
                            // Called when ad is shown.
                            //println("Ad was shown. Kırmızı")
                            loadAd()
                        }

                        override fun onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.
                            kalanZaman(timeLeft)
                            handler.post(runnable)
                            //println("Ad was dismissed. Kırmızı")
                        }
                    }

                    if (mRewardedAd != null) {
                        mRewardedAd?.show(this, OnUserEarnedRewardListener() {
                            fun onUserEarnedReward(rewardItem: RewardItem) {
                                var rewardAmount = rewardItem.amount
                                var rewardType = rewardItem.type
                                //println("User earned the reward.")
                            }
                        })
                    } else {
                        //println("The rewarded ad wasn't ready yet.")
                    }
                }
                alert.setNegativeButton("Hayır") {dialog, which ->
                    finish()
                }
                alert.show()
            }
        } else alertCall()
    }

    private fun characterLocation() {
        runnable = Runnable {

            imageView.x = ((enBasla..enSon).shuffled().last()).toFloat()
            imageView.y = ((boyBasla..boySon).shuffled().last()).toFloat()

            if (15 > (0..100).shuffled().last()) {
                imageView.setColorFilter(Color.argb(100, 255, 0, 0))
                ceza = true
            } else {
                imageView.setColorFilter(Color.argb(0, 0, 0, 0))
                ceza = false
            }
            handler.postDelayed(runnable, speed*2.toLong())
        }
        handler.post(runnable)
    }

    private fun kalanZaman (kalan : Int) {

        countDownTimer = object : CountDownTimer(kalan.toLong(), 1000) {
            override fun onFinish() {

                handler.removeCallbacks(runnable)
                gameOver = true
                alertCall ()
            }
            override fun onTick(p0: Long) {
                kalan!=kalan-1000
                timeLeft = p0.toInt()
                timer.max = toplamZaman
                timer.incrementProgressBy(1000)
            }
        }
         countDownTimer.start()
     }

    private fun character () {
        when (degisken) {
            "token" -> imageView.setImageResource(R.drawable.token)
            "kyle" -> imageView.setImageResource(R.drawable.kyle)
            "kenny" -> imageView.setImageResource(R.drawable.kenny)
            "eric" -> imageView.setImageResource(R.drawable.eric)
            "stan" -> imageView.setImageResource(R.drawable.stan)
            else -> imageView.setImageResource(R.drawable.tweek)
        }
        // gamescreen daki imageview a main activityden ulaşabilsem burayı yazmama hiç gerek kalmaz. Başka aktiviteden viewe ulaşma konusuna bak
    }

    @SuppressLint("SetTextI18n")
    fun get_db_30 () {
        db.collection("Scores30sec").document(auth.currentUser!!.email!!).addSnapshotListener { value, error ->
            if(error!=null) {
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                if (value?.get("score")!=null) {
                    val constant = value.get("score") as Number
                    highest30 = constant.toInt()
                    textViewHigh.text = "En Yüksek Skorun $highest30"
                }
                else if (value?.get("score")==null) {
                    //buraya ilk oyuna özel bir şey eklenebilir
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    fun get_db_60 () {
        db.collection("Scores60sec").document(auth.currentUser!!.email!!).addSnapshotListener { value, error ->
            if(error!=null) {
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                if (value?.get("score")!=null) {
                    val constant = value.get("score") as Number
                    highest60 = constant.toInt()
                    textViewHigh.text = "En Yüksek Skorun $highest60"
                }
                else if (value?.get("score")==null) {
                    //textViewHigh.text = "Henüz Bir Skorun Yok, Hemen Karaktere Dokun !"
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    fun get_db_120 () {
        db.collection("Scores120sec").document(auth.currentUser!!.email!!).addSnapshotListener { value, error ->
            if(error!=null) {
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                if (value?.get("score")!=null) {
                    val constant = value.get("score") as Number
                    highest120 = constant.toInt()
                    textViewHigh.text = "En Yüksek Skorun $highest120"
                }
                else if (value?.get("score")==null) {
                    //textViewHigh.text = "Henüz Bir Skorun Yok, Hemen Karaktere Dokun !"
                }
            }
        }
    }

    fun alertCall () {
        val alert = AlertDialog.Builder(this@GameScreen)
        alert.setTitle("Oyun Bitti")
        alert.setMessage("Şansını Tekrar Denemek İster Misin? Dilersen Ek Süreyi Seçip Reklam Sonrası Kaldığın Yerden Devam Edebilirsin. " +
                "(Reklam gösterimi için Wİ-Fİ bağlantısı gerekebilir)")
        alert.setIcon(R.drawable.pcprincipal)
        alert.setPositiveButton("✓") {dialog, which ->
            this@GameScreen.recreate()
        }
        alert.setNeutralButton("Ek Süre") {dialog, which ->

            mRewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    //println("Ad was shown.")
                    loadAd()
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    handler.post(runnable)
                    if(katsayi>3) katsayi--
                    when (zamanDegisken) {
                        "Scores30sec" -> timeLeft = katsayi*1000
                        "Scores60sec" -> timeLeft = katsayi*2000
                        "Scores120sec" -> timeLeft = katsayi*4000
                    }
                    //println(katsayi)
                    gameOver = false
                    kalanZaman(timeLeft)
                    //println("Ad was dismissed.")
                }
            }

            if (mRewardedAd != null) {
                mRewardedAd?.show(this, OnUserEarnedRewardListener() {
                    fun onUserEarnedReward(rewardItem: RewardItem) {
                        var rewardAmount = rewardItem.amount
                        var rewardType = rewardItem.type
                        //println("User earned the reward.")
                    }
                })
            } else {
                //println("The rewarded ad wasn't ready yet.")
                mRewardedAd?.show(this, OnUserEarnedRewardListener() {
                    fun onUserEarnedReward(rewardItem: RewardItem) {
                        var rewardAmount = rewardItem.amount
                        var rewardType = rewardItem.type
                        //println("User earned the reward.")
                    }
                })
            }
        }
        alert.setNeutralButtonIcon(getDrawable(R.drawable.addtime))
        alert.setNegativeButton("X") {dialog, which ->
            finish()
        }
        alert.show()
    }

    fun loadAd () {

        //println("load ad fun called")

        var adRequest = AdRequest.Builder().build()

        RewardedAd.load(this,"ca-app-pub-8944524190558053/3353552300", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                //println(adError.message)
                mRewardedAd = null
                //println("here")
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                //println("Ad was loaded.")
                mRewardedAd = rewardedAd
            }
        })
    }

}
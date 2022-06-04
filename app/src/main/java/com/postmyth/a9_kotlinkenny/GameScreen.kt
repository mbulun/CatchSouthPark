package com.postmyth.a9_kotlinkenny
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_game_screen.*
import kotlinx.android.synthetic.main.activity_main.*

lateinit var countDownTimer : CountDownTimer
private lateinit var auth : FirebaseAuth
var scoreMap = hashMapOf<String,Any>()

class GameScreen : AppCompatActivity() {

    private lateinit var db : FirebaseFirestore
    private var score = 0
    var runnable = Runnable { }
    var handler = Handler(Looper.getMainLooper())
    private var speed = 800
    var gameOver = false
    private var ceza = false
    private var highest30 = 0
    private var highest60 = 0
    private var highest120 = 0
    var timeLeft = 0
    private var enBasla = 0
    private var enSon = 0
    private var boyBasla = 0
    private var boySon = 0
    var katsayi = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_screen)
        supportActionBar?.hide()

        auth = Firebase.auth
        db = Firebase.firestore
        kalanZaman(toplamZaman)
        character()

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
                println("game fun: cezaya dokundun")
                handler.removeCallbacks(runnable)
                countDownTimer.cancel()
                val alert = AlertDialog.Builder(this)
                alert.setMessage("Kırmızıyken Dokunmaman Gerekiyordu   :( Neyse Ki Bir Reklam İle Kaldığın Yerden Devam Edebilirsin")
                alert.setPositiveButton("Hemen İzle") {dialog, which ->
                    kalanZaman(timeLeft)
                    handler.post(runnable)
                }
                alert.setNegativeButton("Hayır") {dialog, which ->
                    finish()
                }
                alert.show()
            }
        }
    }

    private fun characterLocation() {
        runnable = Runnable {

            imageView.x = ((enBasla..enSon).shuffled().last()).toFloat()
            imageView.y = ((boyBasla..boySon).shuffled().last()).toFloat()

            if (50 > (0..100).shuffled().last()) {
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
                    textViewHigh.text = "Henüz Bir Skorun Yok, Hemen Karaktere Dokun !"
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
                    textViewHigh.text = "Henüz Bir Skorun Yok, Hemen Karaktere Dokun !"
                }
            }
        }
    }

    fun alertCall () {
        val alert = AlertDialog.Builder(this@GameScreen)
        alert.setTitle("Oyun Bitti")
        alert.setMessage("Şansını Tekrar Denemek İster Misin?")
        alert.setIcon(R.drawable.pcprincipal)
        alert.setPositiveButton("Evet") {dialog, which ->
            this@GameScreen.recreate()
        }
        alert.setNeutralButton("süremi uzat") {dialog, which ->
            handler.post(runnable)
            if(katsayi>3) katsayi--
            when (zamanDegisken) {
                "Scores30sec" -> timeLeft = katsayi*1000
                "Scores60sec" -> timeLeft = katsayi*2000
                "Scores120sec" -> timeLeft = katsayi*4000
            }
            println(katsayi)
            gameOver = false
            kalanZaman(timeLeft)
        }
        alert.setNeutralButtonIcon(getDrawable(R.drawable.addtime))
        alert.setNegativeButton("Hayır") {dialog, which ->
            finish()
        }
        alert.show()
    }

}
package com.postmyth.a9_kotlinkenny
import android.graphics.Color
import android.os.*
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_game_screen.*

lateinit var countDownTimer : CountDownTimer
private lateinit var auth : FirebaseAuth
var scoreMap = hashMapOf<String,Any>()

class GameScreen : AppCompatActivity() {

    private lateinit var db : FirebaseFirestore
    var score = 0
    var runnable = Runnable { }
    var handler = Handler(Looper.getMainLooper())
    var speed = 800
    var gameOver = false
    var ceza = false
    var highest30 = 0
    var highest60 = 0
    var highest120 = 0
    var timeLeft = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        println("onCreate Begin")
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
        characterLocation(width, height)

        if (zamanDegisken == "Scores30sec")  get_db_30()
        else if (zamanDegisken == "Scores60sec") get_db_60()
        else if (zamanDegisken == "Scores120sec") get_db_120()
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

        if (zamanDegisken == "Scores30sec") {
            scoreMap.put("score", highest30)
            scoreMap.put("userName",username!!.substringBefore('@'))
            db.collection("Scores30sec").document("${username!!}").set(scoreMap)
        }
        else if (zamanDegisken == "Scores60sec") {
            scoreMap.put("score", highest60)
            scoreMap.put("userName",username!!.substringBefore('@'))
            db.collection("Scores60sec").document("${username!!}").set(scoreMap)
        }
        else if (zamanDegisken == "Scores120sec") {
            scoreMap.put("score", highest120)
            scoreMap.put("userName",username!!.substringBefore('@'))
            db.collection("Scores120sec").document("${username!!}").set(scoreMap)
        }
    }
    override fun onDestroy() {
        //println("onDestroy Begin")
        handler.removeCallbacks(runnable)
        countDownTimer.cancel()
        super.onDestroy()
    }
    override fun onRestart() {
        println("onRestrat Begin")
        super.onRestart()
        handler.post(runnable)
        countDownTimer.start()
    }

    fun game(view: View) {

        if (!gameOver) {
            if (!ceza) {
                score++
                textView.text = "Skorun ${score}"

                if (zamanDegisken == "Scores30sec" && score >= highest30) {
                    highest30 = score
                    textViewHigh.text = "En Yüksek Skorun ${highest30}"
                }
                else if (zamanDegisken == "Scores60sec" && score >= highest60) {
                    highest60 = score
                    textViewHigh.text = "En Yüksek Skorun ${highest60}"
                }
                else if (zamanDegisken == "Scores120sec" && score >= highest120) {
                    highest120 = score
                    textViewHigh.text = "En Yüksek Skorun ${highest120}"
                }

                if (speed > 550) speed -= score * 2
                else if (550 >= speed && speed >= 250) speed -= 15
            }
            else {
                handler.removeCallbacks(runnable)
                countDownTimer.cancel()
                var alert = AlertDialog.Builder(this)
                alert.setMessage("Kırmızıyken Dokunmaman Gerekiyordu   :( Neyse Ki Bir Reklam İle Kaldığın Yerden Devam Edebilirsin")
                alert.setPositiveButton("Hemen İzle") {dialog, which ->
                    kalanZaman(timeLeft)
                    println("timeLeft: $timeLeft")
                    handler.post(runnable)
                }
                alert.show()
            }
        }
    }

    fun characterLocation(width: Int, height: Int) {
        runnable = object : Runnable {
            override fun run() {

                var en_basla = (width * (0.05)).toInt() + 100
                var en_son = (width * (0.95)).toInt() - imageView.height - 50
                var boy_basla = (height * (0.15)).toInt() + 100
                var boy_son = (height * (0.85)).toInt() - imageView.width - 50

                imageView.x = ((en_basla..en_son).shuffled()
                    .last() / (imageView.width / 2) * (imageView.width / 2)).toFloat()
                imageView.y = ((boy_basla..boy_son).shuffled()
                    .last() / (imageView.height / 2) * (imageView.height / 2)).toFloat()

                if (36 > (0..100).shuffled().last()) {
                    imageView.setColorFilter(Color.argb(100, 255, 0, 0))
                    ceza = true
                } else {
                    imageView.setColorFilter(Color.argb(0, 0, 0, 0))
                    ceza = false
                }
                handler.postDelayed(runnable, speed*2.toLong())
            }
        }
        handler.post(runnable)
    }

    fun kalanZaman (kalan : Int) {

        countDownTimer = object : CountDownTimer(kalan.toLong(), 1000) {

            override fun onFinish() {

                handler.removeCallbacks(runnable)
                gameOver = true

                val alert = AlertDialog.Builder(this@GameScreen)
                alert.setTitle("Game Over")
                alert.setMessage("Wanna Play One More Time?")
                alert.setIcon(R.drawable.pcprincipal)

                alert.setPositiveButton("yes") {dialog, which ->
                    this@GameScreen.recreate()
                }

                alert.setNegativeButton("no") {dialog, which ->
                    finish()
                }
                alert.show()
            }
            override fun onTick(p0: Long) {
                kalan!=kalan-1000
                timeLeft = p0.toInt()
                timer.max = toplamZaman
                timer.incrementProgressBy(1000)
                println("toplam zaman ${toplamZaman}   kalan zaman: ${p0}")
            }

        }
         countDownTimer.start()
     }

    fun character () {
        if (degisken=="token")  imageView.setImageResource(R.drawable.token)
        else if (degisken=="kyle")  imageView.setImageResource(R.drawable.kyle)
        else if (degisken=="kenny")  imageView.setImageResource(R.drawable.kenny)
        else if (degisken=="eric") imageView.setImageResource(R.drawable.eric)
        else if (degisken=="stan") imageView.setImageResource(R.drawable.stan)
        else  imageView.setImageResource(R.drawable.tweek)
        // gamescreen daki imageview a main activityden ulaşabilsem burayı yazmama hiç gerek kalmaz. Başka aktiviteden viewe ulaşma konusuna bak
    }

    fun get_db_30 () {
        db.collection("Scores30sec").document(auth.currentUser!!.email!!).addSnapshotListener { value, error ->
            if(error!=null) {
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                if (value?.get("score")!=null) {
                    var constant = value.get("score") as Number
                    highest30 = constant.toInt()
                    textViewHigh.text = "En Yüksek Skorun ${highest30}"
                }
                else if (value?.get("score")==null) {
                    textViewHigh.text = "Henüz Bir Skorun Yok, Hemen Karaktere Dokun !"
                }
            }
        }
    }
    fun get_db_60 () {
        db.collection("Scores60sec").document(auth.currentUser!!.email!!).addSnapshotListener { value, error ->
            if(error!=null) {
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                if (value?.get("score")!=null) {
                    var constant = value.get("score") as Number
                    highest60 = constant.toInt()
                    textViewHigh.text = "En Yüksek Skorun ${highest60}"
                }
                else if (value?.get("score")==null) {
                    textViewHigh.text = "Henüz Bir Skorun Yok, Hemen Karaktere Dokun !"
                }
            }
        }
    }
    fun get_db_120 () {
        db.collection("Scores120sec").document(auth.currentUser!!.email!!).addSnapshotListener { value, error ->
            if(error!=null) {
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                if (value?.get("score")!=null) {
                    var constant = value.get("score") as Number
                    highest120 = constant.toInt()
                    textViewHigh.text = "En Yüksek Skorun ${highest120}"
                }
                else if (value?.get("score")==null) {
                    textViewHigh.text = "Henüz Bir Skorun Yok, Hemen Karaktere Dokun !"
                }
            }
        }
    }
}
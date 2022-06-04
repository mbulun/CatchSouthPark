package com.postmyth.a9_kotlinkenny
// uygulama kimliği  ca-app-pub-8944524190558053~7732007984
// banner test       ca-app-pub-3940256099942544/6300978111
// geçiş test        ca-app-pub-3940256099942544/1033173712
// ödüllü test       ca-app-pub-3940256099942544/5224354917

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

val networkControl = NetworkControl()

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()
        window.statusBarColor = ContextCompat.getColor(this,R.color.black)

        MobileAds.initialize(this) {}
        // Üretim reklamlarını tıklamak, geçersiz trafiğin yol açtığı bir politika ihlaline neden olabilir.
        //Uygulama geliştirme ve testi sırasında, AdMob politikasını ihlal etmeden uygulama kodunuzu doğrulamak için demo reklamları veya test cihazlarını kullanın.

        val handler = Handler()
        handler.postDelayed({
            if (!networkControl.isNetworkAvailable(this)) {
                Toast.makeText(this@SplashScreen,
                    "Lütfen İnternetinizi Açın, Skorunuzun kaydedilmesi ve para kazanabilmeniz için internet gereklidir. " +
                            "Uygulamayı kapatıp internet açıktan sonra tekrar giriş yapınız.",Toast.LENGTH_LONG).show()
            }else {
                if (Firebase.auth.currentUser != null) {
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, LoginPage::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        },2000)
    }
}

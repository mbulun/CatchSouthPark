package com.postmyth.a9_kotlinkenny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseError
import com.google.firebase.FirebaseError.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import kotlinx.android.synthetic.main.activity_login_page.*
import java.lang.Exception

private lateinit var auth : FirebaseAuth

class LoginPage : AppCompatActivity() {

    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        supportActionBar?.hide()
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black))

        auth = Firebase.auth
        db = Firebase.firestore
        if (auth.currentUser != null) {
            intent()
        }

        if (!networkControl.isNetworkAvailable(this)) {
            println("hereeeeeeeeeeee")
            Toast.makeText(this@LoginPage,
                "Lütfen İnternetinizi Açın, Skorunuzun kaydedilmesi ve para kazanabilmeniz için internet gereklidir. " +
                        "Uygulamayı kapatıp internet açıktan sonra tekrar giriş yapınız.",Toast.LENGTH_LONG).show()
            val intent = Intent(this,SplashScreen::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun giris(view: View) {
        val mail = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()
        if(mail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"Lütfen E-Posta ve Şifrenizi Doğru Şekilde Giriniz.",Toast.LENGTH_LONG).show()
        }
        else {
            auth.signInWithEmailAndPassword(mail,password).addOnSuccessListener {
            intent()
            }.addOnFailureListener {
                Toast.makeText(this@LoginPage,it.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    fun kaydol(view: View) {
        val mail = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()
        if(mail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,"Lütfen E-Posta ve Şifrenizi Doğru Şekilde Giriniz. \n Şifreniz en az 6 karakterden oluşmalıdır",Toast.LENGTH_LONG).show()
        }
        else {
            auth.createUserWithEmailAndPassword(mail,password).addOnSuccessListener {
                intent()
            }.addOnFailureListener {
                Toast.makeText(this@LoginPage,it.message,Toast.LENGTH_LONG).show()
            }
        }
    }

    fun intent () {
        val intent = Intent(this@LoginPage,MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}


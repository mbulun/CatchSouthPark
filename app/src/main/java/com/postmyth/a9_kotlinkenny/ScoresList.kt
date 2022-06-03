package com.postmyth.a9_kotlinkenny

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.postmyth.a9_kotlinkenny.databinding.ActivityScoresListBinding
import kotlinx.android.synthetic.main.activity_scores_list.*
import kotlinx.android.synthetic.main.activity_scores_list.button
import kotlinx.android.synthetic.main.activity_scores_list.button2
import kotlinx.android.synthetic.main.activity_scores_list.button3
import kotlinx.android.synthetic.main.recycler_row.view.*
import kotlin.collections.ArrayList

class ScoresList : AppCompatActivity() {

    private lateinit var binding : ActivityScoresListBinding
    private  lateinit var db : FirebaseFirestore
    private var scoreList30 = ArrayList<scoreData> ()
    private var scoreList60 = ArrayList<scoreData> ()
    private var scoreList120 = ArrayList<scoreData> ()
    var adapter : scoreListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoresListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.holo_orange_light))

        db = Firebase.firestore
        get_db_30()
        get_db_60()
        get_db_120()

        if (!networkControl.isNetworkAvailable(this)) {
            Toast.makeText(this@ScoresList,
                "Lütfen İnternetinizi Açın, Skorunuzun kaydedilmesi ve para kazanabilmeniz için internet gereklidir. " +
                        "Uygulamayı kapatıp internet açıktan sonra tekrar giriş yapınız.",Toast.LENGTH_LONG).show()
            val intent = Intent(this,SplashScreen::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun get_db_30 () {

        db.collection("Scores30sec").orderBy("score",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if(error!=null) {
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                if (value!=null) {
                    if (!value.isEmpty) {
                        val documents = value.documents
                        for (document in documents) {
                            val userName = document.get("userName") as String
                            val score = document.get("score") as Number
                            val listMember = scoreData(userName,score)
                            scoreList30.add(listMember)
                        }
                    }
                }
            }
        }
    }
    private fun get_db_60 () {

        db.collection("Scores60sec").orderBy("score",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if(error!=null) {
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                if (value!=null) {
                    if (!value.isEmpty) {
                        val documents = value.documents
                        for (document in documents) {
                            val userName = document.get("userName") as String
                            val score = document.get("score") as Number
                            val listMember = scoreData(userName,score)
                            scoreList60.add(listMember)
                        }
                    }
                }
            }
        }
    }
    private fun get_db_120 () {

        db.collection("Scores120sec").orderBy("score",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if(error!=null) {
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            } else {
                if (value!=null) {
                    if (!value.isEmpty) {
                        val documents = value.documents
                        for (document in documents) {
                            val userName = document.get("userName") as String
                            val score = document.get("score") as Number
                            val listMember = scoreData(userName,score)
                            scoreList120.add(listMember)
                        }
                    }
                }
            }
        }
    }

    class scoreListAdapter : BaseAdapter {

        private var scoreList = ArrayList<scoreData>()
        var context : Context? = null

        constructor(context: Context,scoresList: ArrayList<scoreData>):super() {
            this.scoreList = scoresList
            this.context = context
        }

        override fun getCount(): Int {
            if (scoreList.size<50) return scoreList.size
            else return 50
        }

        override fun getItem(p0: Int): Any {
            if (p0 == 0) {
            }

            return scoreList[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

            val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val x = inflater.inflate(R.layout.recycler_row,null)
            x.rowScore.text = scoreList[p0].score.toString()
            x.rowUserName.text = scoreList[p0].userName

            when {
                p0 == 0 -> x.rowMadal.setImageResource(R.drawable.first)
                p0 == 1 -> x.rowMadal.setImageResource(R.drawable.second)
                p0 == 2 -> x.rowMadal.setImageResource(R.drawable.third)
                p0 < 10 -> x.rowMadal.setImageResource(R.drawable.participate)
            }
            return  x
        }

    }

    private fun scaleButton() {
        button.scaleX = 1F
        button.scaleY = 1F
        button2.scaleX = 1F
        button2.scaleY = 1F
        button3.scaleX = 1F
        button3.scaleY = 1F
    }
    @Suppress("UNUSED_PARAMETER")
    fun show30sec (view: View) {
        scaleButton()
        button.scaleX =1.3F
        button.scaleY =1.3F
        adapter = scoreListAdapter(this@ScoresList,scoreList30)
        listview.adapter =adapter
    }
    @Suppress("UNUSED_PARAMETER")
    fun show60sec (view: View) {
        scaleButton()
        button2.scaleX =1.3F
        button2.scaleY =1.3F
        adapter = scoreListAdapter(this@ScoresList,scoreList60)
        listview.adapter =adapter
    }
    @Suppress("UNUSED_PARAMETER")
    fun show120sec (view: View) {
        scaleButton()
        button3.scaleX =1.3F
        button3.scaleY =1.3F
        adapter = scoreListAdapter(this@ScoresList,scoreList120)
        listview.adapter =adapter
    }

}
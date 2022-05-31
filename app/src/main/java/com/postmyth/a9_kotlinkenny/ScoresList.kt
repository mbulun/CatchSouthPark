package com.postmyth.a9_kotlinkenny

import android.content.Context
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.postmyth.a9_kotlinkenny.databinding.ActivityScoresListBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_scores_list.*
import kotlinx.android.synthetic.main.activity_scores_list.button
import kotlinx.android.synthetic.main.activity_scores_list.button2
import kotlinx.android.synthetic.main.activity_scores_list.button3
import kotlinx.android.synthetic.main.recycler_row.*
import kotlinx.android.synthetic.main.recycler_row.view.*
import kotlinx.coroutines.delay
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class ScoresList : AppCompatActivity() {

    private lateinit var binding : ActivityScoresListBinding
    private  lateinit var db : FirebaseFirestore
    var scoreList30 = ArrayList<scoreData> ()
    var scoreList60 = ArrayList<scoreData> ()
    var scoreList120 = ArrayList<scoreData> ()
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
    }

    fun get_db_30 () {

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
    fun get_db_60 () {

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
    fun get_db_120 () {

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

        var scoreList = ArrayList<scoreData>()
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

            var inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            var x = inflater.inflate(R.layout.recycler_row,null)
            x.rowScore.text = scoreList[p0].score.toString()
            x.rowUserName.text = scoreList[p0].userName

            if(p0 == 0) x.rowMadal.setImageResource(R.drawable.first)
            else if(p0 == 1) x.rowMadal.setImageResource(R.drawable.second)
            else if(p0 == 2) x.rowMadal.setImageResource(R.drawable.third)
            else if(p0 < 10) x.rowMadal.setImageResource(R.drawable.participate)
            return  x
        }

    }

    fun scaleButton() {
        button.scaleX = 1F
        button.scaleY = 1F
        button2.scaleX = 1F
        button2.scaleY = 1F
        button3.scaleX = 1F
        button3.scaleY = 1F
    }
    fun show30sec (view: View) {
        scaleButton()
        button.scaleX =1.3F
        button.scaleY =1.3F
        adapter = scoreListAdapter(this@ScoresList,scoreList30)
        listview.adapter =adapter


    }
    fun show60sec (view: View) {
        scaleButton()
        button2.scaleX =1.3F
        button2.scaleY =1.3F
        adapter = scoreListAdapter(this@ScoresList,scoreList60)
        listview.adapter =adapter
    }
    fun show120sec (view: View) {
        scaleButton()
        button3.scaleX =1.3F
        button3.scaleY =1.3F
        adapter = scoreListAdapter(this@ScoresList,scoreList120)
        listview.adapter =adapter
    }

}
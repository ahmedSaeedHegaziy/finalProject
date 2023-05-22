package com.example.finalcloudproject.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalcloudproject.R
import com.example.finalcloudproject.adapter.TopicAdapterDoctor
import com.example.finalcloudproject.databinding.ActivityArticlesBinding
import com.example.finalcloudproject.model.Article
import com.example.finalcloudproject.model.User
import com.example.finalcloudproject.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_progress.*

class TopicsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArticlesBinding
    lateinit var data:ArrayList<Article>
    lateinit var db: FirebaseFirestore
    //private var progressDialog: ProgressDialog? = null
    private lateinit var progressDialog: Dialog
    lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticlesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //makeCurrentFragment(ArticleFragment())

        db = Firebase.firestore
        data= ArrayList()
        showDialog("جار التحميل ...")
        getAllArticles()



        binding.btnAddArticle.setOnClickListener {
          // makeCurrentFragment(AddArticleFragment())
            val i= Intent(this, AddTopicActivity::class.java)
            startActivity(i)
        }


    }
//    fun makeCurrentFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.container2, fragment).addToBackStack(null)
//            commit()
//        }
//    }


    fun getAllArticles(){
        val sharedP =this.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
//        id = sharedP.getString("idArticle", "0").toString()
        val idCategory = sharedP.getString("idCategory", "0").toString()

        db.collection("Article")
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val idCategory1 = document.getString("idCategory")
                    if (idCategory1 ==idCategory) {
                        val id =document.id
                        val name = document.getString("name")
                        val description = document.getString("description")
                        val img = document.getString("img")
                        val imgName = document.getString("imgName")
                        val audio = document.getString("audio")
                        val audioName = document.getString("audioName")
                        val video = document.getString("video")
                        val videoName = document.getString("videoName")
                        val article= Article(id,name!!,description!!,img!!,video!!,audio!!,imgName!!,videoName!!,audioName!!,idCategory!!)
                        data.add(article)
                    }
                }
                var topicAdapterDoctor = TopicAdapterDoctor(this, data)
                binding.rv.layoutManager = LinearLayoutManager(this)
                binding.rv.adapter = topicAdapterDoctor
                hideDialog()
            }
            .addOnFailureListener {
                Toast.makeText(this,it.message, Toast.LENGTH_SHORT).show()
                hideDialog()
            }
    }

    private fun showDialog(text: String) {
        progressDialog = Dialog(this)
        progressDialog!!.setContentView(R.layout.dialog_progress)
        progressDialog.tv_progress_text.text = text

        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()

    }
    private fun hideDialog() {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
    }



    fun checkUserType(){
        FirebaseFirestore.getInstance().collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                val user = document.toObject(User::class.java)!!

                if (user.userType == "Doctor") {
                    binding.btnAddArticle.visibility = View.VISIBLE
                } else if (user.userType == "Sick") {
                    binding.btnAddArticle.visibility = View.INVISIBLE
                }
                else {
                    binding.btnAddArticle.visibility = View.INVISIBLE
                }
            }
    }
    fun getCurrentUserID(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }


}
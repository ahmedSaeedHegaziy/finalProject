package com.example.finalcloudproject.view

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalcloudproject.R
import com.example.finalcloudproject.adapter.TopicUser
import com.example.finalcloudproject.databinding.ActivityArticlesUserBinding
import com.example.finalcloudproject.model.Article
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_progress.*

class TopicsActivityUser : AppCompatActivity() {
    private lateinit var binding: ActivityArticlesUserBinding
    lateinit var data:ArrayList<Article>
    lateinit var db: FirebaseFirestore
    private lateinit var progressDialog: Dialog
    lateinit var id: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticlesUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = Firebase.firestore
        data= ArrayList()
        showDialog("جار التحميل ...")
        getAllArticles()
    }

    fun getAllArticles(){
        val sharedP =this.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
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
                var articleAdapter = TopicUser(this, data)
                binding.rvUser.layoutManager = LinearLayoutManager(this)
                binding.rvUser.adapter = articleAdapter
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
}
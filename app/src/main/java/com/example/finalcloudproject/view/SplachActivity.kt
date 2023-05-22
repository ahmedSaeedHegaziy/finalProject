package com.example.finalcloudproject.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.example.finalcloudproject.*
import com.example.finalcloudproject.model.User
import com.example.finalcloudproject.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplachActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splach)


        firebaseAuth = FirebaseAuth.getInstance()
        supportActionBar?.hide()

        Handler().postDelayed(
            {
                checkUser()
            }, 1000
        )
    }


    fun getCurrentUserID(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    private fun checkUser() {

        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            val i = Intent(this, WelcomeActivity::class.java)
            startActivity(i)
            finish()

        } else {
            FirebaseFirestore.getInstance().collection(Constants.USERS)
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->

                    val user = document.toObject(User::class.java)!!

                    val sharedPreferences = this.getSharedPreferences(
                        Constants.MYSHOP_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString(
                        Constants.LOGGED_IN_USERNAME,
                        "${user.fullName}"
                    )
                    editor.apply()
                    if (user.userType == "Doctor") {
                        startActivity(Intent(this@SplachActivity, DashBoardAdminActivity::class.java))
                        finish()
                    } else if (user.userType == "Sick") {
                        startActivity(Intent(this@SplachActivity, DashBoardUserActivity::class.java))
                        finish()
                    } else {
                        startActivity(
                            Intent(
                                this@SplachActivity,
                                WelcomeActivity::class.java
                            )
                        )
                        finish()
                    }



                }
        }
    }


}
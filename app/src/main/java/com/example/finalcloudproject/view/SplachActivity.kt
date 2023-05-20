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
                //checkConn()
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

    fun checkConn() {
        //For Connection
        val connManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        val mobileDataConn = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifiConn!!.isConnectedOrConnecting) {
            Toast.makeText(this, "Wifi Conected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Wifi Disconected", Toast.LENGTH_SHORT).show()
        }
        if (mobileDataConn!!.isConnectedOrConnecting) {
            Toast.makeText(this, "mobileData Conected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "mobileData Disconected", Toast.LENGTH_SHORT).show()
        }
    }
}


//        firebaseAuth = FirebaseAuth.getInstance()
//
//        //        val i =Intent(this,MainActivity::class.java)
//       //        startActivity(i) هيك مش حتلحق تبين الواجهة وحينقلني عالمين اكتفتي
//        Handler().postDelayed({
////            val i =Intent(this,signin::class.java)
////            startActivity(i)
////            finish()// عشان لما اعمل رجوع ما يرجعنيي على  splach
//              checkUser()
//        },1000)
//    // حيجيب واجهة السبلاش سكرين لمدة 5 ث وبعدين حيوديني للواجهة
//
//
//    }
//
//    private fun checkUser() {
//
//       val firebaseUser =firebaseAuth.currentUser
//        if (firebaseUser == null ){
//            val i =Intent(this,signin::class.java)
//              startActivity(i)
//            finish()
//
//        }else{
//
//            val ref = FirebaseDatabase.getInstance().getReference("Users")
//            ref.child(firebaseUser.uid)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//
//                    override fun onDataChange(snapshot: DataSnapshot) {
//
//                        val userType = snapshot.child("userType").value
//                        if (userType == "user"){
//                            startActivity(Intent(this@SplachActivity,DashBoardUserActivity::class.java))
//                            finish()
//                        }
//                        else if ( userType == "admin"){
//                            startActivity(Intent(this@SplachActivity,DashBoardAdminActivity::class.java))
//                            finish()
//
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                    }
//
//                })
//
//        }
//    }
//}
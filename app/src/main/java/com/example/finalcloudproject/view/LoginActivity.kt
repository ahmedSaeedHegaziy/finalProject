package com.example.finalcloudproject.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import com.example.finalcloudproject.*
import com.example.finalcloudproject.model.User
import com.example.finalcloudproject.utils.BaseActivity
import com.example.finalcloudproject.utils.Constants
import com.example.shop.firestore.FireStoreClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity(), View.OnClickListener {
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()



        txtForgetPassword.setOnClickListener(this)
        btnReg.setOnClickListener(this)
        btnlog.setOnClickListener(this)

    }

    fun getCurrentUserID(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun userLoggedInSuccess(user: User) {

        hideProgressDialog()

        if (user.profileCompleted == 0) {
            val i = Intent(this@LoginActivity, UserProfileActivity::class.java)
            i.putExtra(Constants.EXTRA_USER_DETAILS, user) // for adding data
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
                        startActivity(
                            Intent(
                                this@LoginActivity,
                                DashBoardAdminActivity::class.java
                            )
                        )
                        finish()
                    } else if (user.userType == "Sick") {
                        startActivity(
                            Intent(
                                this@LoginActivity,
                                DashBoardUserActivity::class.java
                            )
                        )
                        finish()
                    }
                }

        }

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.txtForgetPassword -> {
                    val f = Intent(this, ForgetPasswordActivity::class.java)
                    startActivity(f)
                }

                R.id.btnlog -> {
                    validateLoginDetails()
                }

                R.id.btnReg -> {
                    val i = Intent(this, RegisterActivity::class.java)
                    startActivity(i)
                }
            }
        }
    }

    private fun validateLoginDetails() {

        val email: String = txtemailS.text.toString().trim { it <= ' ' }
        val password: String = txtpasswordS.text.toString().trim { it <= ' ' }


        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showErrorSnackBar("نمط البريد الإلكتروني غير صالح...", true)
        } else if (password.isEmpty()) {
            showErrorSnackBar("كلمة المرور غير صالحة...", true)

        } else {
            loginUser()
        }
    }

    private fun loginUser() {

        showProgressDialog("من فضلك انتظر...")

        val email: String = txtemailS.text.toString().trim { it <= ' ' }
        val password: String = txtpasswordS.text.toString().trim { it <= ' ' }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)

            .addOnSuccessListener {
                FireStoreClass().getUserDetails(this@LoginActivity)
            }
            .addOnFailureListener {
                hideProgressDialog()
                showErrorSnackBar("${it.message.toString()}", true)
            }
    }

    private fun checkUser() {

        //  val firebaseUser: FirebaseUser = it.user!!

        val firebaseUser2 = firebaseAuth.currentUser!!

        val db = FirebaseFirestore.getInstance().collection(Constants.USERS)
        db.document(firebaseUser2.uid)
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseUser2.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val userType = snapshot.child("userType").value

                    if (userType == "user") {
                        val i = Intent(this@LoginActivity, DashBoardUserActivity::class.java)
                        startActivity(i)
                        // startActivity(Intent(this@signin,DashBoardUserActivity::class.java))
                        finish()
                    } else if (userType == "admin") {

                        val i = Intent(this@LoginActivity, DashBoardAdminActivity::class.java)
                        startActivity(i)
                        finish()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }

}
package com.example.finalcloudproject.view

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import com.example.finalcloudproject.R
import com.example.finalcloudproject.model.User
import com.example.finalcloudproject.utils.BaseActivity
import com.example.finalcloudproject.utils.Constants
import com.example.finalcloudproject.utils.PreferanceManeger
import com.example.shop.firestore.FireStoreClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*
import java.util.Calendar

class RegisterActivity : BaseActivity() {
    private var preferanceManeger: PreferanceManeger? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        btnlo.setOnClickListener {
            val l = Intent(this, LoginActivity::class.java)
            startActivity(l)
        }
        btnregdata.setOnClickListener {
            registerUser()
        }

        txtBirthOfDate.setOnClickListener {
            val currentDate = Calendar.getInstance() //  عشان يعرض التاريخ الي موجو بالايميليتور
            val day = currentDate.get(Calendar.DAY_OF_MONTH)
            val month = currentDate.get(Calendar.MONTH)
            val year = currentDate.get(Calendar.YEAR)
            val picker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                    txtBirthOfDate.setText(" $y / ${m + 1} / $d ")
                    //  m +1  عشان احل مشكلة اللي بينقص شهر عشان بيعد من الصفر
                },
                year,
                month,
                day
            ) // يوم شهر سنة
            picker.show() // عشان اظهرها
        }

    }

    private var fullName = ""

    //private var birthOfDate = ""
    private var mobilePhone = ""
    private var email = ""
    private var pass = ""

    private fun registerUser() {

        email = txtemail.text.toString().trim { it <= ' ' }
        fullName = txtfullName.text.toString().trim { it <= ' ' }
        var birthOfDate =
            txtBirthOfDate.text.toString().trim { it <= ' ' }
        mobilePhone = txtPhoneNumbers.text.toString().trim { it <= ' ' }
        pass = txtpassword.text.toString().trim { it <= ' ' }
        val confirmpass = txtconfirmpassword.text.toString().trim { it <= ' ' }

        if (fullName.isEmpty()) {
            showErrorSnackBar("أدخل الاسم كاملا", true)

        } else if (birthOfDate.isEmpty()) {
            showErrorSnackBar("أدخل تاريخ الميلاد", true)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showErrorSnackBar("نمط البريد الإلكتروني غير صالح", true)
        } else if (mobilePhone.isEmpty()) {
            showErrorSnackBar("أدخل رقم الهاتف", true)
        } else if (pass.isEmpty()) {
            showErrorSnackBar("أدخل كلمة المرور", true)

        } else if (confirmpass.isEmpty()) {
            showErrorSnackBar("قم بتأكيد كلمة المرور", true)

        } else if (pass != confirmpass) {
            showErrorSnackBar("كلمة المرور غير متطابقة", true)
        } else if (!checkBox.isChecked) {
            showErrorSnackBar("يرجى قبول شروط الخصوصية والسياسة", true)
        } else {
            createUserAccount()
        }

    }

    private fun createUserAccount() {
        val email: String = txtemail.text.toString().trim { it <= ' ' }
        val password: String = txtpassword.text.toString().trim { it <= ' ' }

        showProgressDialog("من فضلك انتظر...")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)

            .addOnSuccessListener {
                val firebaseUser: FirebaseUser = it.user!!
                val uid = firebaseUser.uid

                val user = User(
                    firebaseUser.uid,
                    txtfullName.text.toString().trim { it <= ' ' },
                    txtBirthOfDate.text.toString().trim { it <= ' ' },
                    txtPhoneNumbers.text.toString().trim { it <= ' ' },
                    txtemail.text.toString().trim { it <= ' ' }

                )
                FireStoreClass().registerUser(this@RegisterActivity, user)

                val sharedPreferences =
                    this.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE)

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(
                    Constants.KEY_USER_ID,
                    uid
                )
                editor.apply()
//                sharedP.getString(Constants.KEY_USER_ID, uid).toString()


            }.addOnFailureListener {
                hideProgressDialog()
                showErrorSnackBar(it.message.toString(), true)

            }
    }

    fun userRegisterationSuccess() {
        hideProgressDialog()
        showErrorSnackBar("لقد تم تسجيلك بنجاح...", false)

    }

    private fun signUp() {
        val database = FirebaseFirestore.getInstance()
        val user = HashMap<String, Any?>()
        database.collection(Constants.KEY_COLLECTION_USERS).add(user)
            .addOnSuccessListener { decRef: DocumentReference ->
                preferanceManeger!!.putString(Constants.KEY_USER_ID, decRef.id)
            }
    }


}
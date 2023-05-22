package com.example.finalcloudproject.view

import android.os.Bundle
import android.widget.Toast
import com.example.finalcloudproject.R
import com.example.finalcloudproject.utils.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forget_password2.*

class ForgetPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password2)


        supportActionBar?.hide()

        btnsubmitF.setOnClickListener {

            val email :String = txtemailid.text.toString().trim{it <= ' '}

            if (email.isEmpty()){
                showErrorSnackBar("يرجى إدخال البريد الاكتروني !!",true)
            }else{
                //لاسترجاع الباسورد
                showProgressDialog("جاري التحميل...")
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)

                    .addOnSuccessListener {
                        hideProgressDialog()

                        Toast.makeText(this,"تم إرسال البريد الإلكتروني بنجاح", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener {
                        hideProgressDialog()
                        showErrorSnackBar("{${it.message}}",true)
                    }

            }
        }
    }
}
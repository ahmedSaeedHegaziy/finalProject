package com.example.finalcloudproject.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.finalcloudproject.R
import com.example.finalcloudproject.model.User
import com.example.finalcloudproject.utils.BaseActivity
import com.example.finalcloudproject.utils.Constants
import com.example.finalcloudproject.utils.GlideLoader
import com.example.shop.firestore.FireStoreClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException
import java.util.Calendar

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageUri: String = ""
    private val mFireStore = FirebaseFirestore.getInstance()
    var job = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        supportActionBar?.hide()

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            //Get the user dwtails from intent as a ParcelableExtra
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        birthOfDatee.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val day = currentDate.get(Calendar.DAY_OF_MONTH)
            val month = currentDate.get(Calendar.MONTH)
            val year = currentDate.get(Calendar.YEAR)
            val picker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                    birthOfDatee.setText(" $y / ${m + 1} / $d ")
                },
                year,
                month,
                day
            )
            picker.show()
        }

        rg_UserType.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == R.id.rb_Sick) {
                hideTextField()
            }

            if (checkedId == R.id.rb_Doctor) {
                showTextField()
            }
        }

        full_namee.setText(mUserDetails.fullName)
        birthOfDatee.setText(mUserDetails.birthOfDate)
        til_mobile_number.setText(mUserDetails.mobilePhone)

        et_email.isEnabled = false
        et_email.setText(mUserDetails.email)


        if (mUserDetails.profileCompleted == 0) {
            txtTitle.text = "اكمل  الملف الشخصي"
            full_namee.isEnabled = false
            birthOfDatee.isEnabled = false
            til_mobile_number.isEnabled = false

        } else {
            setUpActionBar()
            txtTitle.text = "تعديل الملف الشخصي"
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image, iv_user_photo)
            if (mUserDetails.address != "") {
                til_Address.setText(mUserDetails.address)
            }
            if (mUserDetails.gender == Constants.MALE) {
                rb_male.isChecked = true
            } else {
                rb_Female.isChecked = true
            }

            if (mUserDetails.userType == Constants.USERTYPE) {
                rb_Sick.isChecked = true
            } else {
                rb_Doctor.isChecked = true
            }
        }
        full_namee.isEnabled = true
        full_namee.setText(mUserDetails.fullName)

        birthOfDatee.isEnabled = true
        birthOfDatee.setText(mUserDetails.birthOfDate)

        til_mobile_number.isEnabled = true
        til_mobile_number.setText(mUserDetails.mobilePhone)

        et_email.isEnabled = false
        et_email.setText(mUserDetails.email)

        et_email.setText(mUserDetails.email)

        txtDoctorSpecialization.setText(mUserDetails.doctorSpecialization)


        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        btn_save.setOnClickListener(this@UserProfileActivity)

    }

    fun hideTextField() {
        txtDoctorSpecialization.visibility = View.GONE
        txtDoctorSpecialization.text.clear()
    }

    fun showTextField() {
        txtDoctorSpecialization.visibility = View.VISIBLE
    }

    private fun setUpActionBar() {

        setSupportActionBar(toolbar_user_profile_activity)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_color_back_24dp)
        }
        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.iv_user_photo -> {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_save -> {

                    if (validateUserProfileDetails()) {
                        showProgressDialog("من فضلك انتظر...")

                        if (mSelectedImageFileUri != null) {
                            FireStoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri)

                        } else {
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)

            } else {
                showErrorSnackBar(
                    "عفوا ، لقد رفضت للتو إذن التخزين. يمكنك أيضا السماح بذلك من الإعدادات.",
                    true
                )

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                try {
                    mSelectedImageFileUri = data.data!!
                    iv_user_photo.setImageURI(mSelectedImageFileUri)
                    GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, iv_user_photo)

                } catch (e: IOException) {
                    e.printStackTrace()
                    showErrorSnackBar("فشلت الصورة المحددة!", true)
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            showErrorSnackBar("تم إلغاء اختيار الصورة!", true)

        }
    }

    fun updateUserProfileDetails() {

        val userHashMap = HashMap<String, Any>()

        val fullName = full_namee.text.toString().trim { it <= ' ' }
        if (fullName != mUserDetails.fullName) {
            userHashMap[Constants.FULL_NAME] = fullName
        }

        val birthOfDate = birthOfDatee.text.toString().trim { it <= ' ' }
        if (birthOfDate != mUserDetails.birthOfDate) {
            userHashMap[Constants.BIRTHOFDATE] = birthOfDate
        }

        val mobileNumber = til_mobile_number.text.toString().trim { it <= ' ' }
        if (mobileNumber != mUserDetails.mobilePhone) {
            userHashMap[Constants.MOBILE] = mobileNumber
        }


        val address = til_Address.text.toString().trim { it <= ' ' }

        val doctorJob = txtDoctorSpecialization.text.toString().trim { it <= ' ' }


        val gender = if (rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        val userType = if (rb_Sick.isChecked) {
            Constants.SICK
        } else {
            Constants.DOCTOR
        }

//        rb_Sick.isEnabled = false
//        rb_Doctor.isEnabled = false
//        rg_UserType.isEnabled = false

        if (mUserProfileImageUri.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageUri
        }

        if (address.isNotEmpty() && address != mUserDetails.address) {
            userHashMap[Constants.ADDRESS] = address
            //.toLong()
        }

        if (doctorJob.isNotEmpty() && doctorJob != mUserDetails.doctorSpecialization) {
            userHashMap[Constants.DOCTORJOB] = doctorJob
        }

        if (gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }
        userHashMap[Constants.GENDER] = gender



        if (userType.isNotEmpty() && userType != mUserDetails.userType) {
            userHashMap[Constants.USERTYPE] = userType
        }
        userHashMap[Constants.USERTYPE] = userType


        userHashMap[Constants.PROFILE_COMPLETED] = 1

        FireStoreClass().updateUserProfileData(this, userHashMap)

    }


    fun getCurrentUserID(): String {

        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun userProfileUpdateSuccess() {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                val user = document.toObject(User::class.java)!!

                val sharedPreferences = this.getSharedPreferences(
                    Constants.MYSHOP_PREFERENCES,
                    Context.MODE_PRIVATE
                )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(Constants.LOGGED_IN_USERNAME2, user.fullName)
                editor.apply()

                if (user.userType == "Doctor") {
                    val i = Intent(this@UserProfileActivity, DashBoardAdminActivity::class.java)
                    i.putExtra(Constants.EXTRA_USER_DETAILS, user) // for adding data
                    startActivity(i)
                } else if (user.userType == "Sick") {
                    val i = Intent(this@UserProfileActivity, DashBoardUserActivity::class.java)
                    i.putExtra(Constants.EXTRA_USER_DETAILS2, user) // for adding data
                    startActivity(i)
                } else {
                    startActivity(
                        Intent(
                            this@UserProfileActivity,
                            DashBoardUserActivity::class.java
                        )
                    )
                    finish()
                }

                hideProgressDialog()
                Toast.makeText(
                    this,
                    "تم تحديث الملف الشخصي الخاص بك بنجاح.",
                    Toast.LENGTH_SHORT
                ).show()

            }

    }


    private fun validateUserProfileDetails(): Boolean {
        val moNumber = til_mobile_number.text.toString().trim { it <= ' ' }
        return when {
            moNumber.isEmpty() -> {
                showErrorSnackBar("من فضلك اضف رقم الهاتف", true)
                false
            }

            else -> {
                true
            }
        }
    }


    fun imageUploadSuccess(imageURL: String) {
        mUserProfileImageUri = imageURL
        updateUserProfileDetails()

    }

}
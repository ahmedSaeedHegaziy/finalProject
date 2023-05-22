package com.example.shop.firestore

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.finalcloudproject.view.LoginActivity
import com.example.finalcloudproject.view.RegisterActivity
import com.example.finalcloudproject.view.UserProfileActivity
import com.example.finalcloudproject.model.User
import com.example.finalcloudproject.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()
    private lateinit var mProgressDialog: Dialog

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisterationSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()
                Toast.makeText(
                    activity.applicationContext,
                    " فشلت عملية التسجيل",
                    Toast.LENGTH_SHORT
                ).show()
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

    fun getUserDetails(activity: Activity, readCategoryList: Boolean = false) {

        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                if (document != null) {
                    val user = document.toObject(User::class.java)!!

                    val sharedPreferences = activity.getSharedPreferences(
                        Constants.MYSHOP_PREFERENCES,
                        Context.MODE_PRIVATE
                    )

                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString(Constants.LOGGED_IN_USERNAME, user.fullName)
                    editor.apply()

                    when (activity) {

                        is LoginActivity -> {
                            activity.userLoggedInSuccess(user)
                        }
                    }
                }

            }
            .addOnFailureListener {

                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                }
            }

    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()

                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Toast.makeText(activity.applicationContext, "فشل عملية تحديث البروفايل", Toast.LENGTH_SHORT).show()
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileUri: Uri?) {

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "." + Constants.getFileExtension(
                activity,
                imageFileUri
            )
        )
        sRef.putFile(imageFileUri!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )
                taskSnapshot.metadata!!.reference!!.downloadUrl

                    .addOnSuccessListener { uri ->
                        when (activity) {
                            is UserProfileActivity -> {

                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.hideProgressDialog()
                            }
                        }
                    }
            }
    }

}

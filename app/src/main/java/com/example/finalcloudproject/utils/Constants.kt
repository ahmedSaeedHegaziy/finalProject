package com.example.finalcloudproject.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    const val USERS: String = "User_Care_App"
    const val SUBSCRIBE: String = "User_Subscribe_Care_App"
    const val CATEGORY: String = "Category"

    const val MYSHOP_PREFERENCES: String = "MyShopPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"
    const val LOGGED_IN_USERNAME2: String = "logged_in_username"
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val EXTRA_USER_DETAILS2: String = "extra_user_details2"


    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1


    const val MALE: String = "Male"
    const val FEMALE: String = "Female"


    const val SICK: String = "Sick"
    const val DOCTOR: String = "Doctor"


    const val FULL_NAME: String = "fullName"
    const val BIRTHOFDATE: String = "birthOfDate"
    const val ADDRESS: String = "address"
    const val DOCTORJOB: String = "doctorSpecialization"


    const val MOBILE: String = "mobilePhone"
    const val GENDER: String = "gender"
    const val IMAGE: String = "image"
    const val USERTYPE: String = "userType"
    const val DESCRIPTION: String = "Description"


    const val PROFILE_COMPLETED: String = "profileCompleted"
    const val USER_PROFILE_IMAGE = "User_Profile_Image_Care"



    ///////////////////////////////////////////////////////////////
    //const val KEY_COLLECTION_USERS = "users"
    const val KEY_COLLECTION_USERS = "User"

    const val KEY_NAME = "fullName"

    const val KEY_EMAIL = "email"
    const val KEY_PASSWORD = "password"
    const val KEY_PREFERENCE_NAME = "chatAppPreference"
    const val KEY_IS_SIGNED_IN = "isSignedIn"
    const val KEY_USER_ID = "userId"
    const val KEY_IMAGE = "image"
    const val KEY_FCM_TOKEN = "fcmToken"
    const val KEY_USER = "user"
    const val KEY_COLLECTION_CHAT = "chat"
    const val KEY_SENDER_ID = "senderId"
    const val KEY_RECEIVER_ID = "receiverId"
    const val KEY_MESSAGE = "message"
    const val KEY_TIMESTAMP = "timestamp"
    const val KEY_COLLECTION_CONVERSATIONS = "conversations"
    const val KEY_SENDER_NAME = "senderName"
    const val KEY_RECEIVER_NAME = "receiverName"
    const val KEY_SENDER_IMAGE = "senderImage"
    const val KEY_RECEIVER_IMAGE = "receiverImage"
    const val KEY_LAST_MESSAGE = "lastMessage"
    const val KEY_AVAILABILITY = "availability"
    const val KEY_TYPE = "availability"

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }


    fun getFileExtension(activity: Activity,uri: Uri?) : String? {
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))

    }
}
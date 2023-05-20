package com.example.finalcloudproject.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
 var id : String = "",
 var fullName : String = "",
 val birthOfDate : String = "",
 val mobilePhone : String = "",
 var email : String = "",
 var image : String = "",
 val profileCompleted : Int = 0,
 val address : String = "",
 val doctorSpecialization : String = "",
 val gender : String = "",
 val userType : String = "",

 ) : Parcelable

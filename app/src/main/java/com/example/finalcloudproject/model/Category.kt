package com.example.finalcloudproject.model
import com.google.firebase.firestore.DocumentId

data class Category(@DocumentId var id:String,var name :String,var img:String,var imgName:String,var description:String,var doctorName:String?
,var isSubscribe :Int =0
)
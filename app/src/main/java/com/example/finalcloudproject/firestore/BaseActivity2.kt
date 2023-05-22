package com.example.finalcloudproject.firestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.finalcloudproject.R
import com.example.finalcloudproject.utils.Constants
import com.example.finalcloudproject.utils.PreferanceManeger
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

 open class BaseActivity2 : AppCompatActivity() {
    private var documentReference: DocumentReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferanceManeger = PreferanceManeger(applicationContext)
        val database = FirebaseFirestore.getInstance()

        documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
            .document(preferanceManeger.getString(Constants.KEY_USER_ID).toString())
    }

    override fun onPause() {
        super.onPause()
        documentReference!!.update(Constants.KEY_AVAILABILITY, 0)
    }

    override fun onResume() {
        super.onResume()
        documentReference!!.update(Constants.KEY_AVAILABILITY, 1)
    }
}
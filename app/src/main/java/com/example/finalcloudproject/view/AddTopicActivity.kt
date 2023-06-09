package com.example.finalcloudproject.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.finalcloudproject.R
import com.example.finalcloudproject.databinding.ActivityAddArticleBinding
import com.example.finalcloudproject.model.Article
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.dialog_progress.*
import java.io.ByteArrayOutputStream

class AddTopicActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddArticleBinding
    lateinit var db: FirebaseFirestore
    private var fileImgURI: Uri? = null
    private var fileVideoURI: Uri? = null
    private val PICK_IMAGE_REQUEST = 111
    private val PICK_Video_REQUEST = 222
    var imageURI: Uri? = null
    var videoURI: Uri? = null
    lateinit var imageRef: StorageReference
    lateinit var videoRef: StorageReference
    lateinit var storageRef: StorageReference
    lateinit var imgName: String
    lateinit var videoName: String
    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore
        val storage = Firebase.storage
        storageRef = storage.reference
        imageRef = storageRef.child("images")
        videoRef = storageRef.child("videos")

        binding.bAddImg.setOnClickListener {
            selsectImg()
        }
        binding.bAddVideo.setOnClickListener {
            selsectVideo()
        }


        binding.btnAdd.setOnClickListener {
            uploadImg()
        }

    }
    fun selsectImg() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    fun selsectVideo() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "video/*"
        startActivityForResult(intent, PICK_Video_REQUEST)
    }



    fun uploadImg() {
        showDialog("تحميل المقال ...")
        val bitmap = (binding.img1.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()
        imgName = System.currentTimeMillis().toString() + "_wimages.png"
        val childRef =
            imageRef.child(imgName)
        var uploadTask = childRef.putBytes(data)
        uploadTask
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "فشل تحميل الصورة${exception.message})",
                    Toast.LENGTH_SHORT
                )
                    .show()
                hideDialog()
            }
            .addOnSuccessListener {
                childRef.downloadUrl.addOnSuccessListener { uri0 ->
                    fileImgURI = uri0
                    if (fileImgURI != null) {
                        uploadVideo()
                    }
                }
            }
    }

    fun uploadVideo() {
        videoName =
            System.currentTimeMillis().toString() + "_wvideos.mp4"
        val childRef1 =
            videoRef.child(videoName)
        val uploadTask1 = childRef1.putFile(videoURI!!)
        uploadTask1
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    " فشل تحميل الفيديو${exception.message})",
                    Toast.LENGTH_SHORT
                )
                    .show()
                hideDialog()
            }
            .addOnSuccessListener {
                childRef1.downloadUrl.addOnSuccessListener { uri ->
                    fileVideoURI = uri
                    if (fileVideoURI != null) {
                    }
                }
            }
    }


    private fun showDialog(text: String) {
        progressDialog = Dialog(this)
        progressDialog!!.setContentView(R.layout.dialog_progress)
        progressDialog.tv_progress_text.text = text

        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()

    }
    private fun hideDialog() {
        if (progressDialog.isShowing)
            progressDialog.dismiss()
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageURI = data!!.data
            if (imageURI != null) {
                binding.img.setImageResource(R.drawable.ic_baseline_done_24)
                binding.img1.setImageURI(imageURI)
            }
        }
        if (requestCode == PICK_Video_REQUEST && resultCode == Activity.RESULT_OK) {
            videoURI = data!!.data
            if (videoURI != null) {
                binding.imgVideo.setImageResource(R.drawable.ic_baseline_done_24)
            }
        }

    }

}
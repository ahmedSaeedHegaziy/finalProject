package com.example.finalcloudproject.admin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.finalcloudproject.R
import com.example.finalcloudproject.view.DashBoardAdminActivity
import com.example.finalcloudproject.databinding.FragmentAddCategoryBinding
import com.example.finalcloudproject.model.Category
import com.example.finalcloudproject.model.User
import com.example.finalcloudproject.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.dialog_progress.*
import kotlinx.android.synthetic.main.fragment_profile_admin.*
import java.io.ByteArrayOutputStream

class AddCategoryFragment : Fragment() {
    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!
    lateinit var db: FirebaseFirestore
    //private var progressDialog: ProgressDialog? = null
    private lateinit var progressDialog: Dialog

    private var fileURI: Uri? = null
    private val PICK_IMAGE_REQUEST = 111
    var imageURI: Uri? = null
    lateinit var d: Activity
    private val mFireStore = FirebaseFirestore.getInstance()
    private lateinit var mUserDetails: User


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.firestore
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef = storageRef.child("images")
        d = (activity as DashBoardAdminActivity)
        binding.bAddImg.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
        getUserDetails()
        binding.btnAdd.setOnClickListener {
            showDialog("تحميل التصنيف ...")
            val name = binding.txtName.text.toString()
            val description = binding.txtDescription.text.toString()
            val docName = binding.txtDoctorName.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty() && imageURI != null&& docName.isNotEmpty()
            ) {
                val bitmap = (binding.imageView.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val data = baos.toByteArray()
                val imgName = System.currentTimeMillis().toString() + "_omrimages.png"
                val childRef =
                    imageRef.child(imgName)
                var uploadTask = childRef.putBytes(data)
                uploadTask
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                            d,
                            "فشل التحميل${exception.message})",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        hideDialog()
                    }
                    .addOnSuccessListener {
                        childRef.downloadUrl.addOnSuccessListener { uri ->
                            fileURI = uri
                            if (fileURI != null) {
                                val category =
                                    Category("", name, fileURI.toString(), imgName, description,docName)
                                db.collection("Category")
                                    .add(category)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            d,
                                            "تم التحميل!",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        binding.txtName.text.clear()
                                        binding.txtDescription.text.clear()
                                        hideDialog()
                                        (d as DashBoardAdminActivity).makeCurrentFragment(
                                            HomeAdminFragment()
                                        )
                                        //(d as MainActivity).makeCurrentFragment(CategoryFragment())

                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            d,
                                            "فشل تحميل${it.message}",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        hideDialog()
                                    }
                            } else {
                                Toast.makeText(d, "حاول مرة اخرى!", Toast.LENGTH_SHORT).show()
                                hideDialog()
                            }
                        }
                    }
            } else {
                Toast.makeText(d, "أكمل البيانات!", Toast.LENGTH_SHORT).show()
                hideDialog()
            }

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

    private fun getUserDetails() {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)!!
                    userDetailsSuccess(user)
                }
            }
    }

    fun userDetailsSuccess(user: User) {
        mUserDetails = user
        binding.txtDoctorName.setText(user.fullName)
    }

    private fun showDialog(text: String) {
        progressDialog = Dialog(d)
        progressDialog!!.setContentView(R.layout.dialog_progress)
        progressDialog.tv_progress_text.text = text

        progressDialog!!.setCancelable(false)
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()

//        progressDialog = ProgressDialog(MainActivity@ d)
//        progressDialog!!.setMessage("تحميل التصنيف ...")
//        progressDialog!!.setCancelable(false)
//        progressDialog!!.show()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun hideDialog() {
        if (progressDialog.isShowing)
        progressDialog.dismiss()
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

//        if (progressDialog!!.isShowing)
//            progressDialog!!.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            imageURI = data!!.data
            binding.imageView.setImageURI(imageURI)
        }
    }
}
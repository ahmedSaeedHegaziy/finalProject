package com.example.finalcloudproject.doctor

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
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
import com.example.finalcloudproject.databinding.FragmentEditCategoryBinding
import com.example.finalcloudproject.model.Category
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_progress.*
import java.io.ByteArrayOutputStream

class
EditCategoryFragment : Fragment(), java.io.Serializable {

    private var _binding: FragmentEditCategoryBinding? = null
    private val binding get() = _binding!!
    lateinit var db: FirebaseFirestore
    lateinit var d: Activity
    private var fileURI: Uri? = null
    lateinit var imageRef: StorageReference
    lateinit var storageRef: StorageReference
    private val PICK_IMAGE_REQUEST = 111
    var imageURI: Uri? = null
    lateinit var name: String
    lateinit var imgName: String
    lateinit var img: String
    lateinit var description: String
    lateinit var doctorName: String
    lateinit var id: String

    private lateinit var progressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditCategoryBinding.inflate(inflater, container, false)
        binding.txtDoctorName.isEnabled = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        d = (activity as DashBoardAdminActivity)
        showDialog("حفظ التغيرات  ...")
        db = Firebase.firestore
        val storage = Firebase.storage
        storageRef = storage.reference
        imageRef = storageRef.child("images")

        getCategory()
        EditCategoy()
        binding.btnDelete.setOnClickListener {
            val dialog = AlertDialog.Builder(activity)
            dialog.setTitle("حذف")
            dialog.setMessage("do you want delete this Caegory!")
            dialog.setPositiveButton("yes") { _, _ ->
                db.collection("Category").document(id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(activity, "تم الحذف", Toast.LENGTH_SHORT).show()
                        (d as DashBoardAdminActivity).makeCurrentFragment(HomeAdminFragment())
                        imageRef.child(imgName).delete()
                        Toast.makeText(activity, "فشل الحذف", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "فشل الحذف", Toast.LENGTH_SHORT).show()

                    }
            }
            dialog.setNegativeButton("No") { dis, _ ->
                dis.dismiss()
            }
            dialog.create().show()
        }
    }


    fun getCategory() {
        val sharedP = d.getSharedPreferences("MyPref", Context.MODE_PRIVATE)

        id = sharedP.getString("idCategory", "0").toString()
        db.collection("Category").document(id).get()
            .addOnSuccessListener {
                name = it.getString("name").toString()
                description = it.getString("description").toString()
                doctorName = it.getString("doctorName").toString()
                img = it.getString("img").toString()
                imgName = it.getString("imgName").toString()
                binding.txtName.setText(name)
                binding.txtDescription.setText(description)
                binding.txtDoctorName.setText(doctorName)
                Picasso.get().load(img).into(binding.imageView)
                hideDialog()
            }

    }

    fun EditCategoy() {
        binding.bAddImg.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.btnAdd.setOnClickListener {
            showDialog("حفظ التغيرات  ...")
            name = binding.txtName.text.toString()
            description = binding.txtDescription.text.toString()
            doctorName = binding.txtDoctorName.text.toString()

            if (name.isNotEmpty() && description.isNotEmpty() && imageURI != null && doctorName.isNotEmpty()) {
                val bitmap = (binding.imageView.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                val data = baos.toByteArray()
                val imgName2 = System.currentTimeMillis().toString() + "_wimages.png"
                val childRef =
                    imageRef.child(imgName2)
                var uploadTask = childRef.putBytes(data)
                uploadTask
                    .addOnFailureListener { exception ->
                        Toast.makeText(
                            d,
                            "فشل تحميل الصورة${exception.message}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        hideDialog()
                    }
                    .addOnSuccessListener {
                        childRef.downloadUrl.addOnSuccessListener { uri ->
                            fileURI = uri
                            uploadCategory(fileURI.toString(), imgName2)
                            imageRef.child(imgName).delete()
                        }
                    }
            } else if (name.isNotEmpty() && description.isNotEmpty() && imageURI == null) {
                uploadCategory(img, imgName)
            } else {
                Toast.makeText(d, "أكمل البيانات!", Toast.LENGTH_SHORT).show()
                hideDialog()
            }

        }
    }

    fun uploadCategory(img1: String, imgName1: String) {
        if (img1 != null && imgName1 != null) {
            val category = Category("", name, img1, imgName1, description, doctorName)
            db.collection("Category").document(id)
                .set(category)
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
                    (d as DashBoardAdminActivity).makeCurrentFragment(HomeAdminFragment())
                }
                .addOnFailureListener {
                    Toast.makeText(
                        d,
                        "فشل التحميل${it.message}",
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

    private fun showDialog(text: String) {
        progressDialog = Dialog(d)
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
            binding.imageView.setImageURI(imageURI)
        }
    }
}
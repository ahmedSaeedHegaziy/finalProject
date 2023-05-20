package com.example.finalcloudproject.admin

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalcloudproject.R
import com.example.finalcloudproject.adapter.Notifications
import com.example.finalcloudproject.databinding.FragmentNotificationsBinding
import com.example.finalcloudproject.model.Subscriber
import com.example.finalcloudproject.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_progress.*

class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    lateinit var db: FirebaseFirestore
    lateinit var data: ArrayList<Subscriber>
    //private var progressDialog: ProgressDialog? = null
    private lateinit var progressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.firestore
        data = ArrayList()
        showDialog("جار التحميل ...")
        getSubscribeCategory()
    }

    fun getSubscribeCategory() {
        db.collection(Constants.SUBSCRIBE)
            .get()
            .addOnSuccessListener {
                for (document in it) {
                    val id = document.id
                    val name = document.getString("name")
                    val description = document.getString("description")
                    val img = document.getString("img")
                    val imgName = document.getString("imgName")
                    val doctorName = document.getString("doctorName")
                    val userName = document.getString("userName")
//                    val isSubscribe = document.getString("isSubscribe")

                    val notifications = Subscriber(
                        id,
                        name!!,
                        img!!,
                        imgName!!,
                        "",
                        "",
                        userName!!,
//                        isSubscribe!!.toInt(),
                    )
                    data.add(notifications)

                    // }
                }
                var notificationsUserAdapter = Notifications(requireActivity(), data)
                binding.rvNotifications.layoutManager = LinearLayoutManager(requireActivity())
                binding.rvNotifications.adapter = notificationsUserAdapter
                hideDialog()
            }
            .addOnFailureListener {
                Toast.makeText(requireActivity(), it.message, Toast.LENGTH_SHORT).show()
                hideDialog()
            }
    }

    private fun showDialog(text: String) {
        progressDialog = Dialog(requireContext())
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
}
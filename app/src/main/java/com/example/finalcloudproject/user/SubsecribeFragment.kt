package com.example.finalcloudproject.user

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalcloudproject.R
import com.example.finalcloudproject.adapter.Subscribe
import com.example.finalcloudproject.databinding.FragmentSubsecribeBinding
import com.example.finalcloudproject.model.Category
import com.example.finalcloudproject.utils.Constants
import com.example.finalcloudproject.view.DashBoardUserActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_progress.*


class SubsecribeFragment : Fragment() {

    private var _binding: FragmentSubsecribeBinding? = null
    private val binding get() = _binding!!
    lateinit var db: FirebaseFirestore
    lateinit var data: ArrayList<Category>
    //private var progressDialog: ProgressDialog? = null
    private lateinit var progressDialog: Dialog
    lateinit var d: Activity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubsecribeBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = Firebase.firestore
        data = ArrayList()
        d = (activity as DashBoardUserActivity)
        showDialog("جار التحميل ...")
        getSubscribeCategory()
    }

    fun getSubscribeCategory() {
        val sharedP = requireActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val idSubscribe = sharedP.getString("idSubscribeCategory", "0").toString()

        db.collection(Constants.SUBSCRIBE)
            .get()
            .addOnSuccessListener {
                for (document in it) {
//                    val idSubscribe1 = document.getString("idSubscribeCategory")
//                    if (idSubscribe1 == idSubscribe) {
                    val id = document.id
                    val name = document.getString("name")
                    val description = document.getString("description")
                    val img = document.getString("img")
                    val imgName = document.getString("imgName")
                    val doctorName = document.getString("doctorName")
//                    val isSubscribe = document.getString("isSubscribe")

                    val category = Category(
                        id,
                        name!!,
                        img!!,
                        imgName!!,
                        description!!,
                        doctorName!!,
                    )
                    data.add(category)

                    // }
                }
                var subscribeUserAdapter = Subscribe(requireActivity(), data)
                binding.recyclerSub.layoutManager = LinearLayoutManager(requireActivity())
                binding.recyclerSub.adapter = subscribeUserAdapter
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

// قم بتحميل قائمة العناصر من مصدر البيانات الخاص بك

// الحصول على مثيل SharedPreferences
//        val sharedPreferences =
//            requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        // الحصول على مجموعة معرف العناصر المفضلة
//        val favourites =
//            sharedPreferences.getStringSet("favourites", mutableSetOf()) ?: mutableSetOf()
//
//        // تحديث علامة isFavourite بناءً على القيمة الحالية لـ favourites
//        items.forEach { item ->
//            item.isSubscribe = favourites.contains(item.id.toString())
//        }
//
//        // إنشاء وتعيين محول RecyclerView
//        val adapter = CategoryUserAdapter(requireActivity(), items as ArrayList<Category>)
//        binding.recyclerSub.adapter = adapter

//        root.btnaddd.setOnClickListener {
//            onAddButtonClicked()
//        }
//        root.btnedit.setOnClickListener {
//            Toast.makeText(activity,"Edit Button Clicked", Toast.LENGTH_SHORT).show()
//
//        }
//        root.btnaddcategory.setOnClickListener {
////            val i = Intent(activity, AddCategory::class.java)
//         //   startActivity(i)
//
//        }

//    private fun setAnimation(clicked:Boolean) {
//        if (!clicked){
//            btnedit.visibility = View.VISIBLE
//            btnaddcategory.visibility = View.VISIBLE
//        }else{
//            btnedit.visibility = View.INVISIBLE
//            btnaddcategory.visibility = View.INVISIBLE
//        }
//    }
//
//    private fun setClickable(clicked: Boolean){
//        if (!clicked){
//            btnedit.isClickable = true
//            btnaddcategory.isClickable = true
//        }else{
//            btnedit.isClickable =false
//            btnaddcategory.isClickable = false
//        }
//    }
//
//    private fun setVisibility(clicked:Boolean) {
//        if (!clicked) {
//            btnedit.startAnimation(fromBottom)
//            btnaddcategory.startAnimation(fromBottom)
//            btnaddd.startAnimation(rotateOpen)
//        } else {
//            btnedit.startAnimation(toBottom)
//            btnaddcategory.startAnimation(toBottom)
//            btnaddd.startAnimation(rotateClose)
//        }
//    }

//    private fun onAddButtonClicked() {
//        setVisibility(clicked)
//        setAnimation(clicked)
//        setClickable(clicked)
//        clicked = !clicked // ==         if (!clicked) clicked = true else clicked = false
//    }

// arraylist to hold categories
//    private lateinit var categoryArrayList:ArrayList<UserCare>

//private lateinit var userArrayList:ArrayList<ModelCategory>

//adapter
//private lateinit var adapterCategory: AdapterCategory

//private val rotateOpen: Animation by lazy {
//    AnimationUtils.loadAnimation(
//        activity,
//        R.anim.rotate_open_anim
//    )
//}
//private val rotateClose: Animation by lazy {
//    AnimationUtils.loadAnimation(
//        activity,
//        R.anim.rotate_close_anim
//    )
//}
//private val fromBottom: Animation by lazy {
//    AnimationUtils.loadAnimation(
//        activity,
//        R.anim.from_bottom_anim
//    )
//}
//private val toBottom: Animation by lazy {
//    AnimationUtils.loadAnimation(
//        activity,
//        R.anim.to_bottom_anim
//    )
//}
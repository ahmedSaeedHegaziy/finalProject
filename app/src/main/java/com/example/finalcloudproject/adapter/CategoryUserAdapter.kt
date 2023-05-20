package com.example.finalcloudproject.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.finalcloudproject.R
import com.example.finalcloudproject.databinding.LayoutViewUserBinding
import com.example.finalcloudproject.model.Category
import com.example.finalcloudproject.model.User
import com.example.finalcloudproject.utils.Constants
import com.example.finalcloudproject.view.TopicsActivityUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.io.Serializable

class CategoryUserAdapter(var activity: Activity, var data: ArrayList<Category>) :
    RecyclerView.Adapter<CategoryUserAdapter.MyViewHolder>(), Serializable {
    private val mFireStore = FirebaseFirestore.getInstance()
    private lateinit var mUserDetails: User

    class MyViewHolder(var binding: LayoutViewUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LayoutViewUserBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    @SuppressLint("MutatingSharedPrefs", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Picasso.get().load(data[position].img).into(holder.binding.imgCategory)
        holder.binding.tvName.setText(data[position].name)
        holder.binding.tvDescription.setText(data[position].description)
        holder.binding.doctorName.setText(data[position].doctorName)
//        holder.binding.btnSubscribe.setImageResource(data[position].isSubscribe)

        holder.binding.cardView.setOnClickListener {
            val sharedP = activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val edit = sharedP!!.edit()
            edit.putString("idCategory", data[position].id)
            edit.apply()
            val i = Intent(activity, TopicsActivityUser::class.java)
            activity.startActivity(i)
        }

        val subsc = data[position]
        if (subsc.isSubscribe == 0) {
            holder.binding.btnSubscribe.setOnClickListener {
                val isSubscribe = 1.also { subsc.isSubscribe = it }
                if (data[position].isSubscribe == R.drawable.subscribe1 || data[position].isSubscribe == 1) {
                    val sharedP =
                        activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
                    val edit = sharedP!!.edit()
                    edit.putString("idSubscribeCategory", data[position].isSubscribe.toString())
                    edit.apply()
                    val catHashMap = HashMap<String, Any>()
                    val img = subsc.imgName
                    val img2 = subsc.img
                    val name = subsc.name
                    val desc = subsc.description
                    val docName = subsc.doctorName
                    mFireStore.collection(Constants.USERS)
                        .document(getCurrentUserID())
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val user = document.toObject(User::class.java)!!
                                val userName =user.fullName
//                                val userName = "${user.fullName} تم الأشتراك بواسطة :"

                                if (name.isNotEmpty()) {
                                    catHashMap["name"] = name
                                }
                                if (desc.isNotEmpty()) {
                                    catHashMap["description"] = desc
                                }
                                if (img.isNotEmpty()) {
                                    catHashMap["imgName"] = img
                                }
                                if (img2.isNotEmpty()) {
                                    catHashMap["img"] = img2
                                }
                                if (docName!!.isNotEmpty()) {
                                    catHashMap["doctorName"] = docName
                                }
                                if (userName.isNotEmpty()) {
                                    catHashMap["userName"] = userName
                                }
                                if (isSubscribe == 1) {
                                    catHashMap["isSubscribe"] = isSubscribe
                                    //R.drawable.ic_fav1
                                }
                                mFireStore.collection(Constants.SUBSCRIBE)
                                    .document(subsc.id)
                                    .set(catHashMap)
                                    .addOnSuccessListener {
                                        holder.binding.btnSubscribe.visibility = View.INVISIBLE
                                       // holder.binding.btnUnSubscribe.visibility = View.VISIBLE
//                            holder.binding.btnSubscribe.setImageResource(R.drawable.subscribe2)
                                        Toast.makeText(
                                            activity,
                                            "تم الأشتراك بنجاح....",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        notifyDataSetChanged()
                                    }
                                    .addOnFailureListener {
//                            holder.binding.btnSubscribe.setImageResource(R.drawable.subscribe1)
                                        Toast.makeText(
                                            activity,
                                            "فشلت عملية الأشتراك ....",
                                            Toast.LENGTH_SHORT
                                        )
                                    }

                            }
                        }


//                holder.binding.btnSubscribe.visibility = View.INVISIBLE
//                holder.binding.btnUnSubscribe.visibility = View.VISIBLE
//                notifyDataSetChanged()
                }

                if (data[position].isSubscribe == R.drawable.subscribe2 || data[position].isSubscribe == 0
                ) {
                    holder.binding.btnSubscribe.visibility = (View.INVISIBLE)
//                holder.binding.fav3.visibility = (View.VISIBLE)
                    //holder.binding.btnUnSubscribe.visibility = (View.INVISIBLE)
                    notifyDataSetChanged()

                }
            }
        }

//        holder.binding.btnUnSubscribe.setOnClickListener {
//            if (data[position].isSubscribe == R.drawable.subscribe2 || data[position].isSubscribe == 0) {
//                holder.binding.btnSubscribe.visibility = (View.VISIBLE)
//                holder.binding.btnUnSubscribe.visibility = (View.INVISIBLE)
//                notifyDataSetChanged()
//            }
//        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun getUserDetails() {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)!!
                    val userName = user.fullName
                }
            }
    }

//

    fun checkUserType() {
        FirebaseFirestore.getInstance().collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)!!
                if (user.userType == "Doctor") {

                } else if (user.userType == "Sick") {

                } else {
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
}

//val item = data[position]
//
//if (item.isSubscribe == true) {
//    holder.binding.btnSubscribe.visibility = View.VISIBLE
//    holder.binding.btnUnSubscribe.visibility = View.GONE
//} else {
//    holder.binding.btnSubscribe.visibility = View.GONE
//    holder.binding.btnUnSubscribe.visibility = View.VISIBLE
//}

//var onSubscribeClickListener: AdapterView.OnItemClickListener? = null
//
//interface onItemClickListener {
//    fun onItemClicked(position: Int, model: Category)
//}
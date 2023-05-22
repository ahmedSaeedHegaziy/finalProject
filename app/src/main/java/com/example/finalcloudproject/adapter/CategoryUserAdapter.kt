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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Picasso.get().load(data[position].img).into(holder.binding.imgCategory)
        holder.binding.tvName.setText(data[position].name)
        holder.binding.tvDescription.setText(data[position].description)
        holder.binding.doctorName.setText(data[position].doctorName)

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
                                }
                                mFireStore.collection(Constants.SUBSCRIBE)
                                    .document(subsc.id)
                                    .set(catHashMap)
                                    .addOnSuccessListener {
                                        holder.binding.btnSubscribe.visibility = View.INVISIBLE
                                        Toast.makeText(
                                            activity,
                                            "تم الأشتراك بنجاح",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        notifyDataSetChanged()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            activity,
                                            "فشلت عملية الأشتراك ",
                                            Toast.LENGTH_SHORT
                                        )
                                    }

                            }
                        }



                }

                if (data[position].isSubscribe == R.drawable.subscribe2 || data[position].isSubscribe == 0
                ) {
                    holder.binding.btnSubscribe.visibility = (View.INVISIBLE)

                    notifyDataSetChanged()

                }
            }
        }


    }

    override fun getItemCount(): Int {
        return data.size
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


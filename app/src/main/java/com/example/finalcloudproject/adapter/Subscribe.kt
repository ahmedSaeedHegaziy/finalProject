package com.example.finalcloudproject.adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalcloudproject.databinding.LayoutSubscribeViewBinding
import com.example.finalcloudproject.model.Category
import com.example.finalcloudproject.utils.Constants
import com.example.finalcloudproject.view.TopicsActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.io.Serializable

class Subscribe(var activity: Activity, var data: ArrayList<Category>) :
    RecyclerView.Adapter<Subscribe.MyViewHolder>(), Serializable {
    class MyViewHolder(var binding: LayoutSubscribeViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LayoutSubscribeViewBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val subsc = data[position]
        Picasso.get().load(data[position].img).into(holder.binding.imgCategory)
        holder.binding.tvName.setText(data[position].name)
        holder.binding.tvDescription.setText(data[position].description)
        holder.binding.tvDoctorName.setText(data[position].doctorName)

        holder.binding.cardView.setOnClickListener {
            val sharedP = activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val edit = sharedP!!.edit()
            edit.putString("idCategory", data[position].id)
            edit.apply()
            val i = Intent(activity, TopicsActivity::class.java)
            activity.startActivity(i)
        }
        holder.binding.btnUnSub.setOnClickListener {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("الغاء الاشتراك")
            builder.setMessage("هل تريد الغاء الاشتراك؟؟")
            builder.setPositiveButton("نعم") { dialog, which ->
                FirebaseFirestore.getInstance().collection(Constants.SUBSCRIBE).document(subsc.id)
                    .delete().addOnSuccessListener {
                        data.removeAt(position)
                        notifyDataSetChanged()
                    }
            }
            builder.setNegativeButton("لا") { dialog, which ->
                dialog.cancel()
            }
            notifyItemChanged(holder.adapterPosition)
            builder.show()
        }
    }
    override fun getItemCount(): Int {
        return data.size
    }
    }
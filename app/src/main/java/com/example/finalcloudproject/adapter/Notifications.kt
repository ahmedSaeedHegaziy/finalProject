package com.example.finalcloudproject.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalcloudproject.databinding.LayoutViewNotificationBinding
import com.example.finalcloudproject.model.Subscriber
import com.squareup.picasso.Picasso
import java.io.Serializable

class Notifications(var activity: Activity, var data: ArrayList<Subscriber>) :

    RecyclerView.Adapter<Notifications.MyViewHolder>(), Serializable {
    class MyViewHolder(var binding: LayoutViewNotificationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = LayoutViewNotificationBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val subsc = data[position]
        Picasso.get().load(data[position].img).into(holder.binding.imgCategory)
       holder.binding.tvUserName.setText("${data[position].name}تم الاشتراك بواسطة : ")
        holder.binding.tvDiseaseName.setText("بمرض : ${data[position].name}")
        holder.binding.tvUserName.setText("تم الأشتراك بواسطة : ${data[position].userName}")

    }

    override fun getItemCount(): Int {
        return data.size
    }




}
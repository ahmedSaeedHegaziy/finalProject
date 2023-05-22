package com.example.finalcloudproject.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.finalcloudproject.R
import com.example.finalcloudproject.databinding.LayoutViewArticleBinding
import com.example.finalcloudproject.model.Article
import com.example.finalcloudproject.view.EditArticleActivity
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class TopicAdapterDoctor(var activity: Activity, var data: ArrayList<Article>) :

    RecyclerView.Adapter<TopicAdapterDoctor.MyViewHolder>() {
    class MyViewHolder(var binding: LayoutViewArticleBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LayoutViewArticleBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MyViewHolder, re: Int) {

        holder.binding.tv.setText(data[re].description)
        holder.binding.tv2.setText(data[re].name)
        Picasso.get().load(data[re].img).into(holder.binding.image)
        video(holder, re)
        holder.binding.tvEdit.setOnClickListener {
            val sharedP = activity.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val edit = sharedP!!.edit()
            edit.putString("idArticle", data[re].id)
            edit.apply()

            val i = Intent(activity, EditArticleActivity::class.java)
            activity.startActivity(i)
            activity.finish()
        }


    }


    fun video(holder: MyViewHolder, p: Int) {
        holder.binding.videoView.setVideoURI(data[p].video.toUri())
        holder.binding.imgPlayVideo.setOnClickListener {
            if (holder.binding.videoView.isPlaying) {
                holder.binding.videoView.pause()
                holder.binding.imgPlayVideo.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            } else {
                holder.binding.videoView.start()
                holder.binding.imgPlayVideo.setImageResource(R.drawable.ic_pause_black_24dp)
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


    override fun getItemCount(): Int {
        return data.size
    }

}
package com.example.finalcloudproject.adapter

import android.app.Activity
import android.media.MediaPlayer
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.finalcloudproject.R
import com.example.finalcloudproject.databinding.LayoutViewArticleUserBinding
import com.example.finalcloudproject.model.Article
import com.squareup.picasso.Picasso

class TopicUser(var activity: Activity, var data: ArrayList<Article>) :
    RecyclerView.Adapter<TopicUser.MyViewHolder>() {
    class MyViewHolder(var binding: LayoutViewArticleUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LayoutViewArticleUserBinding.inflate(activity.layoutInflater, parent, false)
        return MyViewHolder(binding)
    }



    override fun onBindViewHolder(holder: MyViewHolder, p: Int) {
        holder.binding.tv.setText(data[p].description)
        holder.binding.tv2.setText(data[p].name)
        Picasso.get().load(data[p].img).into(holder.binding.image)
        video(holder, p)
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



    override fun getItemCount(): Int {
        return data.size
    }

}
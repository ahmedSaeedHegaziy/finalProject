package com.example.finalcloudproject.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalcloudproject.adapter.User.UserViewHolder
import com.example.finalcloudproject.databinding.ItemContanerUserBinding
import com.example.finalcloudproject.listeners.UserListeners
import com.example.finalcloudproject.model.User
import com.example.finalcloudproject.utils.GlideLoader

class User(
    val context: Context,
    private val users: List<User>,
    private val userLesteners: UserListeners
) :
    RecyclerView.Adapter<UserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemContainerUserBinding =
            ItemContanerUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(itemContainerUserBinding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.setUserData(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class UserViewHolder(var binding: ItemContanerUserBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun setUserData(user: User) {
            binding.textName.text = user.fullName
            binding.textEmail.text = user.email


            GlideLoader(context).loadUserPicture(
                user.image,
                binding!!.imageProfile
            )
            binding.root.setOnClickListener { v: View? -> userLesteners.onUserClicked(user) }
        }

    }


}
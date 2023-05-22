package com.example.finalcloudproject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalcloudproject.databinding.ItemContainerRecevedMessageBinding
import com.example.finalcloudproject.databinding.ItemContanerSendMessageBinding
import com.example.finalcloudproject.model.ChatMessage
import com.example.finalcloudproject.utils.GlideLoader


class Chat(

    val context: Context,
    private val chatMessages: List<ChatMessage>,


    private val senderId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            SentMessageViewHolder(
                ItemContanerSendMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,

                    false
                )
            )
        } else {
            ReceivedMessageViewHolder(
                ItemContainerRecevedMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            (holder as SentMessageViewHolder).setData(chatMessages[position])
        } else {

            (holder as ReceivedMessageViewHolder).setData(
                chatMessages[position],

            )
        }
    }

    override fun getItemCount(): Int {
        return chatMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatMessages[position].senderId == senderId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    internal class SentMessageViewHolder(private val binding: ItemContanerSendMessageBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun setData(chatMessage: ChatMessage) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime
        }
    }

    inner class ReceivedMessageViewHolder(private val binding: ItemContainerRecevedMessageBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ) {
        fun setData(chatMessage: ChatMessage

        ) {
            binding.textMessage.text = chatMessage.message
            binding.textDateTime.text = chatMessage.dateTime

            GlideLoader(context).loadUserPicture(
                chatMessage.conversationImage!!,
                binding.imageProfile
            )

        }
    }

    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }
}
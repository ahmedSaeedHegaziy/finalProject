package com.example.finalcloudproject.listeners

import com.example.finalcloudproject.model.User

interface ConversationListeners {
    fun onConversionClicked(user: User?)
}
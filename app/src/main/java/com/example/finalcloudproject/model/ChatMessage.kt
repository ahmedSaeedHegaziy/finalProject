package com.example.finalcloudproject.model

import java.util.Date

class ChatMessage {
    @JvmField
    var dateObject: Date? = null
    var conversationId: String? = null
    var conversationName: String? = null
    var conversationImage: String? = null
    @JvmField
    var senderId: String? = null
    @JvmField
    var receiverId: String? = null
    @JvmField
    var message: String? = null
    @JvmField
    var dateTime: String? = null


}
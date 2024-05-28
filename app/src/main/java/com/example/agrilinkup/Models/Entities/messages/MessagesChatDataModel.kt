package com.example.agrilinkup.Models.Entities.messages
data class MessagesChatDataModel(
    val senderUid: String? = null,
    val messageType: String? = null,
    var smsContent: String? = null,
    var videoThumbnail:String? = null,
    var demoSmsContent:String? = null,
    val timestamp: Long = System.currentTimeMillis()
)


package com.example.agrilinkup.Models.Entities.messages

data class MessagesListDataModel(
    val name:String,
    val profileImgLink:String,
    val uid:String,
    val userSms:String? = null,
    val smsTime:Long = System.currentTimeMillis()
){
    constructor() : this("","","")
}
package com.example.agrilinkup.Models.Entities.messages

data class ChatUserListDataModel(
    var name:String,
    var profileImgLink:String,
    var UserUid:String,
    var productId:String,
    var userSms:String? = null,
    var docID:String="",
    var smsTime:Long = System.currentTimeMillis()
){
    constructor() : this("","","","")
}
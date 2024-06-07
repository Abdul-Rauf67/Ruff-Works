package com.example.agrilinkup.Models.Entities

class CartModel(
    var user_ID_ofProduct:String,
    var docId_ofProduct: String ,
    var cart_Id:String="" ,
    var timestamp: Long = System.currentTimeMillis(),
)
{
    constructor() : this(
        "",""
    )
}
package com.example.agrilinkup.Models.Entities

import android.os.Parcelable
import androidx.core.location.LocationRequestCompat.Quality
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductModel(
    var productImgeUri:String,
    var productTitle:String,
    var productSubTitle: String,
    var inputproductCategory: String,
    var productQuality: String,
    var UserLocation:String,
    var pricePerUnit:String,
    var productQuantity:String,
    var productTotalUnits:String,
    var productDiscription:String,
    var productStatus:String,
    var productSeasonStartDate:String,
    var productseasonEndDate:String,
    var user_ID:String,
    val timestamp: Long = System.currentTimeMillis(),
    var docId: String = "",
    var productSellerName:String="",
    var productImageName:String="",
    var productUploadDateTime:String="",
    var cartItemID:String="",
    var tempValue:String="",
    val cart_Id:String="",
    val docId_ofProduct:String="",
    val user_ID_ofProduct:String=""
): Parcelable {
    constructor() : this("","","","",
        "","","","","","",
        "","","","")
}
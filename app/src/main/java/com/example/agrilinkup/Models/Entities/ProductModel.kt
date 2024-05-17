package com.example.agrilinkup.Models.Entities

import androidx.core.location.LocationRequestCompat.Quality

data class ProductModel(
    var productImgeUri:String,
    var productTitle:String,
    var productSubTitle: String,
    var productQuality: String,
    var UserLocation:String,
    var pricePerUnit:String,
    var productQuantity:String,
    var productTotalUnits:String,
    var productDiscription:String,
    var productStatus:String,
    var productSeason:String,
    var productSeasonStartDate:String,
    var productseasonEndDate:String
){
    constructor() : this("","","","",
        "","","","","",
        "","","","")
}
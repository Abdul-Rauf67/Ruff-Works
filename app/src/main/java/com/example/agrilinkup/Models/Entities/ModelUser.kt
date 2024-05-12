package com.example.agrilinkup

import com.google.android.gms.common.internal.AccountType

data class ModelUser(
    val email: String,
    val password: String,
    val fullName: String,
    val state: String,
    val district:String,
    val address: String,
    val accountType: String,
    val phoneNumber: String,
    var profileImageUri: String,
    var docId: String = ""
) {
    constructor() : this("", "", "", "", "", "", "","","")
}
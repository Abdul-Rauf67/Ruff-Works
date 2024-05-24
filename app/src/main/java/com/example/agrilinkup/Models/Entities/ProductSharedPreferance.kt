package com.example.agrilinkup.Models.Entities

import android.content.Context
import android.content.SharedPreferences
import com.example.agrilinkup.ModelUser
import com.example.agrilinkup.Models.Entities.ProductModel
import com.google.firebase.firestore.auth.User
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ProductSharedPreferance @Inject constructor(@ApplicationContext private val context: Context) {
    companion object {
        private const val PREF_NAME = "MyPreference"
        private const val KEY_USER = "productData"
    }


    private val myPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private val gson = Gson()

    fun saveTempProduct(product:ProductModel){
        val json = gson.toJson(product)
        myPreferences.edit().putString(KEY_USER, json).apply()
    }

    fun getProduct():ProductModel?{
        val json = myPreferences.getString(KEY_USER, null)
        return gson.fromJson(json, ProductModel::class.java)
    }

}
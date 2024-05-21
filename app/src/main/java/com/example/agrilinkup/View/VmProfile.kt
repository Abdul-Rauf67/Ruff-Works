package com.example.agrilinkup.View

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrilinkup.ModelUser
import com.example.agrilinkup.Models.Entities.ProductModel
import com.example.agrilinkup.ui.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VmProfile @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    val updateStatus = profileRepository.updateStatus
    val userPicUpdate = profileRepository.profilePicUpdate
    val addProductStatus = profileRepository.addProductStatus
    val fetchProduct = profileRepository.fetchProducts
    val fetchProductsListings=profileRepository.fetchProductsListings

    fun updateUser(user: ModelUser) {
        viewModelScope.launch {
            profileRepository.updateUser(user)
        }
    }

    fun updatePic(uri: Uri) {
        viewModelScope.launch {
            profileRepository.uploadUpdatedProfilePhoto(uri)
        }
    }

    fun addProduct(product: ProductModel) {
        viewModelScope.launch {
            profileRepository.uploadProductImage(product)
        }
    }

    fun fetchProducts() {
        viewModelScope.launch {
            profileRepository.fetchProducts()
        }
    }

    fun fetchProductsListings(currentUserUid: String){
        viewModelScope.launch {
            profileRepository.fetchProductListings(currentUserUid)
        }
    }
}
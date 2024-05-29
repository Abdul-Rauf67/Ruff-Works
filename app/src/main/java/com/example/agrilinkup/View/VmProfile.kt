package com.example.agrilinkup.View

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrilinkup.ModelUser
import com.example.agrilinkup.Models.Entities.CartModel
import com.example.agrilinkup.Models.Entities.ProductModel
import com.example.agrilinkup.Models.Entities.messages.ChatUserListDataModel
import com.example.agrilinkup.ui.ProfileRepository
import com.example.flame.ui.fragments.messages.userlist.MessagesUserListFragment
import com.example.flame.ui.fragments.messages.userlist.MessagesUserListVm
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
    val updateProductStatus=profileRepository.updateProductStatus
    val prodcutPicUpdateStatus=profileRepository.prodcutPicUpdateStatus
    val AddToCortProducts=profileRepository.AddToCortProducts
    val fetchCartProducts=profileRepository.fetchCartProducts
    val AddUserToUserChatList=profileRepository.AddUserToUserChatList


    fun updateUser(user: ModelUser) {
        viewModelScope.launch {
            profileRepository.updateUser(user)
        }
    }

    fun updateProduct(product:ProductModel){
        viewModelScope.launch {
            profileRepository.updateProduct(product)
        }
    }
    fun uploadUpdateProductImage(uri: Uri,oldImageUri: Uri,product: ProductModel){
        viewModelScope.launch {
            profileRepository.uploadUpdateProductImage(uri,oldImageUri,product)
        }
    }
    fun updatePic(uri: Uri,oldImageUri:Uri) {
        viewModelScope.launch {
            profileRepository.uploadUpdatedProfilePhoto(uri,oldImageUri)
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

    fun addProductToCart(cartModel: CartModel) {
        viewModelScope.launch {
            profileRepository.addProductToCart(cartModel)
        }
    }
    fun fetchProductsOfCart() {
        viewModelScope.launch {
            profileRepository.fetchProductsOfCart()
        }
    }
    fun addUserToUserChatList(chatUserListDataModel: ChatUserListDataModel) {
        viewModelScope.launch {
            profileRepository.addUserToUserChatList(chatUserListDataModel)
        }
    }
//    fun fetchUserFormUserChatList() {
//        viewModelScope.launch {
//            profileRepository.fetchUserFormUserChatList()
//        }
//    }
}
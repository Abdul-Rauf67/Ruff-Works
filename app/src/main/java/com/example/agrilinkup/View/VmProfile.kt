package com.example.agrilinkup.View

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrilinkup.ModelUser
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
    val addVehicleStatus = profileRepository.addVehicleStatus
   // val fetchVehicles = profileRepository.fetchVehicles

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

//    fun addVehicle(vehicle: ModelVehicle) {
//        viewModelScope.launch {
//            profileRepository.addVehicle(vehicle)
//        }
//    }
//
//    fun fetchVehicles() {
//        viewModelScope.launch {
//            profileRepository.fetchVehicles()
//        }
//    }
}
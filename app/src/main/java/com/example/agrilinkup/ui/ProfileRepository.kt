package com.example.agrilinkup.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.agrilinkup.ModelUser
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val prefs: PreferenceManager,
    private val storage: StorageReference,
    private val context: Context
) {
    private val uid = auth.currentUser?.uid!!

    private val _updateStatus = MutableLiveData<DataState<Nothing>>()
    val updateStatus: LiveData<DataState<Nothing>> = _updateStatus

    private val _profilePicUpdateStatus = MutableLiveData<DataState<Nothing>>()
    val profilePicUpdate: LiveData<DataState<Nothing>> = _profilePicUpdateStatus

    private val _addVehicleStatus = MutableLiveData<DataState<Nothing>>()
    val addVehicleStatus: LiveData<DataState<Nothing>> = _addVehicleStatus

//    private val _fetchVehicles = MutableLiveData<DataState<List<ModelVehicle>>>()
//    val fetchVehicles: LiveData<DataState<List<ModelVehicle>>> = _fetchVehicles

    fun updateUser(user: ModelUser) {
        _updateStatus.value = DataState.Loading
        db.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                prefs.saveUserData(user)
                _updateStatus.value = DataState.Success()
            }
            .addOnFailureListener {
                _updateStatus.value = DataState.Error(it.message!!)
            }
    }

    fun uploadUpdatedProfilePhoto(uri: Uri) {
        _profilePicUpdateStatus.value = DataState.Loading
        val ref = storage.child("profile_images/${System.currentTimeMillis()}")

        val bitmap = ImageUtils.uriToBitmap(context, uri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        ref.putBytes(data)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ref.downloadUrl.addOnSuccessListener { downloadedUri ->
                        if (downloadedUri != null) {
                            updateProfilePhoto(downloadedUri.toString())
                        }
                    }
                } else {
                    _updateStatus.value = DataState.Error(it.exception?.message!!)
                }
            }
    }

    private fun updateProfilePhoto(uri: String) {
        db.collection("users").document(uid)
            .update("profileImageUri", uri)
            .addOnSuccessListener {
                updateUserPref(uri)
                _profilePicUpdateStatus.value = DataState.Success()
            }
            .addOnFailureListener {
                _profilePicUpdateStatus.value = DataState.Error(it.message!!)
            }
    }

    private fun updateUserPref(uri: String) {
        val user = prefs.getUserData()
        if (user != null) {
            user.profileImageUri = uri
            prefs.saveUserData(user)
        }
    }

    //Vehicles
//
//    fun addVehicle(vehicle: ModelVehicle) {
//        _addVehicleStatus.value = DataState.Loading
//        db.collection("users_vehicles").document(uid).collection("vehicles")
//            .add(vehicle)
//            .addOnSuccessListener {
//                _addVehicleStatus.value = DataState.Success()
//            }
//            .addOnFailureListener {
//                _addVehicleStatus.value = DataState.Error(it.message!!)
//            }
//    }
//
//    fun fetchVehicles() {
//        _fetchVehicles.value = DataState.Loading
//        db.collection("users_vehicles").document(uid).collection("vehicles")
//            .addSnapshotListener { value, error ->
//                if (error != null) {
//                    _fetchVehicles.value = DataState.Error(error.message!!)
//                    return@addSnapshotListener
//                }
//
//                val vehiclesList = mutableListOf<ModelVehicle>()
//                for (data in value?.documents!!) {
//                    val vehicle = data.toObject(ModelVehicle::class.java)
//                    if (vehicle != null) {
//                        val docId = data.id
//                        vehicle.docId = docId
//                        vehiclesList.add(vehicle)
//                    }
//                }
//
//                _fetchVehicles.value = DataState.Success(vehiclesList)
//            }
//    }

}
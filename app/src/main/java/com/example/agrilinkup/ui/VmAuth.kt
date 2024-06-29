package com.example.agrilinkup.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.ImageUtils
import com.example.agrilinkup.ModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class VmAuth @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val storage: StorageReference,
    private val preferenceManager: PreferenceManager,
    private val context: Context
) : ViewModel() {

    private val _signUpStatus = MutableLiveData<DataState<Nothing>>()
    val signUpStatus: LiveData<DataState<Nothing>> = _signUpStatus

    private val _loginStatus = MutableLiveData<DataState<Nothing>>()
    val loginStatus: LiveData<DataState<Nothing>> = _loginStatus

    suspend fun signUpWithEmailPass(user: ModelUser) {
        withContext(Dispatchers.Main) {
            _signUpStatus.value = DataState.Loading
        }
        try {
            auth.createUserWithEmailAndPassword(user.email, user.password).addOnSuccessListener {
                uploadProfileImage(user)
            }.addOnFailureListener { exception ->
                when (exception) {
                    is FirebaseAuthUserCollisionException -> {
                        _signUpStatus.value = DataState.Error("User already exists.")
                        Toast.makeText(context, "User Already exists", Toast.LENGTH_SHORT).show()
                    }

                    is FirebaseAuthInvalidCredentialsException -> {
                        _signUpStatus.value = DataState.Error("Invalid email or password format.")
                        Toast.makeText(
                            context,
                            "Invalid email or password format.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    else -> {
                        _signUpStatus.value = DataState.Error("Authentication failed.")
                        Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            _signUpStatus.value = DataState.Error("Authentication failed: ${e.message}")
            Toast.makeText(context, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun uploadProfileImage(user: ModelUser) {
        val ref = storage.child("profile_images/${System.currentTimeMillis()}")

        val bitmap = ImageUtils.uriToBitmap(context, user.profileImageUri.toUri())
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        ref.putBytes(data).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                ref.downloadUrl.addOnSuccessListener { uri1 ->
                    user.profileImageUri = uri1.toString()
                    user.imageName = getImageNameFromUri(uri1)
                    Toast.makeText(context, "image uploaded", Toast.LENGTH_SHORT).show()
                    addUserToDb(user)
                }
            } else {
                _signUpStatus.value = DataState.Error(task.exception.toString())
                Toast.makeText(
                    context,
                    "At image upload    " + task.exception.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getImageNameFromUri(uri: Uri): String {
        val path = uri.path
        return path?.substring(path.lastIndexOf('/') + 1) ?: ""
    }

    private fun addUserToDb(user: ModelUser) {
        db.collection("users").document(auth.currentUser?.uid!!).set(user)
            .addOnSuccessListener {
                getUserFromDbAndSaveToPref()
                Toast.makeText(context, "User added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                _signUpStatus.value = DataState.Error(it.message!!)
            }
    }

    fun loginWithEmailPass(email: String, password: String) {
        _loginStatus.value = DataState.Loading
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            getUserFromDbAndSaveToPref()
        }.addOnFailureListener { exception ->
            if (exception is FirebaseAuthInvalidCredentialsException) {
                _loginStatus.value = DataState.Error("Wrong credentials Or not signed up.")
            } else {
                _loginStatus.value = DataState.Error("Authentication failed.")
            }
        }
    }

    private fun getUserFromDbAndSaveToPref() {
        db.collection("users").document(auth.currentUser?.uid!!)
            .get().addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(ModelUser::class.java)
                if (user != null) {
                    val docId = documentSnapshot.id
                    user.docId = docId
                    preferenceManager.saveUserData(user)
                }
                _loginStatus.value = DataState.Success()
                _signUpStatus.value = DataState.Success()
            }.addOnFailureListener {
                _loginStatus.value = DataState.Error(it.message!!)
                _signUpStatus.value = DataState.Error(it.message!!)
            }
    }
}

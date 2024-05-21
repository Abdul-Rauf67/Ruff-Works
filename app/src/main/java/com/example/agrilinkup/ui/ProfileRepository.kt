package com.example.agrilinkup.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.agrilinkup.ModelUser
import com.example.agrilinkup.Models.Entities.ProductModel
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.ImageUtils
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
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

    private val _addProdductStatus = MutableLiveData<DataState<Nothing>>()
    val addProductStatus: LiveData<DataState<Nothing>> = _addProdductStatus

    private val _fetchProducts = MutableLiveData<DataState<List<ProductModel>>>()
    val fetchProducts: LiveData<DataState<List<ProductModel>>> = _fetchProducts

    private val _fetchProductsListings = MutableLiveData<DataState<List<ProductModel>>>()
    val fetchProductsListings: LiveData<DataState<List<ProductModel>>> = _fetchProductsListings

    lateinit var userName: String

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

    //Products

    fun uploadProductImage(product: ProductModel) {
        val ref = storage.child("Product_images/${System.currentTimeMillis()}")

        val bitmap = ImageUtils.uriToBitmap(context, product.productImgeUri.toUri())
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        ref.putBytes(data).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                ref.downloadUrl.addOnSuccessListener { uri1 ->
                    product.productImgeUri = uri1.toString()
                    Toast.makeText(context, "image uploaded", Toast.LENGTH_SHORT).show()
                    addProductToDB(product)
                }
            } else {
                _addProdductStatus.value = DataState.Error(task.exception.toString())
                Toast.makeText(
                    context,
                    "At image upload    " + task.exception.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun addProductToDB(product: ProductModel) {
        _addProdductStatus.value = DataState.Loading
        db.collection("users_products").document(uid).collection("products")
            .add(product)
            .addOnSuccessListener {
                _addProdductStatus.value = DataState.Success()
            }
            .addOnFailureListener {
                _addProdductStatus.value = DataState.Error(it.message!!)
            }
    }

    fun fetchProducts() {
        _fetchProducts.value = DataState.Loading
        db.collection("users_products").document(uid).collection("products")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _fetchProducts.value = DataState.Error(error.message!!)
                    return@addSnapshotListener
                }

                val productsList = mutableListOf<ProductModel>()
                for (data in value?.documents!!) {
                    val product = data.toObject(ProductModel::class.java)
                    if (product != null) {
                        val docId = data.id
                        product.docId = docId
                        productsList.add(product)
                    }
                }

                _fetchProducts.value = DataState.Success(productsList)
            }
    }


    fun fetchProductListings() {
        _fetchProductsListings.value = DataState.Loading
        db.collectionGroup("products")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _fetchProductsListings.value = DataState.Error(error.message!!)
                    return@addSnapshotListener
                }

                val productsList = mutableListOf<ProductModel>()
                for (data in value?.documents!!) {
                    val product = data.toObject(ProductModel::class.java)
                    if (product != null) {
                        val docId = data.id
                        product.docId = docId
                        productsList.add(product)
                    }
                }

                _fetchProductsListings.value = DataState.Success(productsList)
            }
    }

    fun fetchProductListings(currentUserUid: String) {
        _fetchProductsListings.value = DataState.Loading
        db.collectionGroup("products")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _fetchProductsListings.value = DataState.Error(error.message!!)
                    return@addSnapshotListener
                }

                if (value == null || value.isEmpty) {
                    _fetchProductsListings.value = DataState.Success(emptyList())
                    return@addSnapshotListener
                }

                val productsList = mutableListOf<ProductModel>()
                val tasks = mutableListOf<Task<DocumentSnapshot>>()

                for (data in value.documents) {
                    val product = data.toObject(ProductModel::class.java)
                    if (product != null) {
                        // Extract the user ID from the document path to determine the owner
                        val docPath = data.reference.path
                        val pathSegments = docPath.split("/")
                        val userId = pathSegments[pathSegments.indexOf("users_products") + 1]

                        // Skip products belonging to the current user
                        if (userId != currentUserUid) {
                            val task = db.collection("users").document(userId).get()
                            tasks.add(task)

                            task.addOnSuccessListener { documentSnapshot ->
                                val user = documentSnapshot.toObject(ModelUser::class.java)
                                if (user != null) {
                                    val userName = user.fullName
                                    val docId = data.id
                                    product.docId = docId
                                    product.user_ID = userId
                                    product.productSellerName = userName
                                    productsList.add(product)
                                }
                            }
                        }
                    }
                }

                // Wait for all tasks to complete
                Tasks.whenAllComplete(tasks).addOnCompleteListener {
                    _fetchProductsListings.value = DataState.Success(productsList)
                }
            }
    }

}




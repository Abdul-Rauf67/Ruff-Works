package com.example.agrilinkup.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.agrilinkup.ModelUser
import com.example.agrilinkup.Models.Entities.CartModel
import com.example.agrilinkup.Models.Entities.ProductModel
import com.example.agrilinkup.Models.Entities.messages.ChatUserListDataModel
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.ImageUtils
import com.example.flame.ui.fragments.messages.userlist.MessagesUserListFragment
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.lang.Exception
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val prefs: PreferenceManager,
    private val storage: StorageReference,
    private val context: Context
) {
    private val uid = auth.currentUser?.uid!!

    lateinit var imageNameUri: String

    private val _updateStatus = MutableLiveData<DataState<Nothing>>()
    val updateStatus: LiveData<DataState<Nothing>> = _updateStatus

    private val _profilePicUpdateStatus = MutableLiveData<DataState<Nothing>>()
    val profilePicUpdate: LiveData<DataState<Nothing>> = _profilePicUpdateStatus

    private val _porductPicUpdateStatus = MutableLiveData<DataState<Nothing>>()
    val prodcutPicUpdateStatus: LiveData<DataState<Nothing>> = _porductPicUpdateStatus

    private val _updateProductStatus = MutableLiveData<DataState<Nothing>>()
    val updateProductStatus: LiveData<DataState<Nothing>> = _updateProductStatus

    private val _addProdductStatus = MutableLiveData<DataState<Nothing>>()
    val addProductStatus: LiveData<DataState<Nothing>> = _addProdductStatus

    private val _fetchProducts = MutableLiveData<DataState<List<ProductModel>>>()
    val fetchProducts: LiveData<DataState<List<ProductModel>>> = _fetchProducts

    private val _fetchProductsListings = MutableLiveData<DataState<List<ProductModel>>>()
    val fetchProductsListings: LiveData<DataState<List<ProductModel>>> = _fetchProductsListings

    private val _AddToCartProducts = MutableLiveData<DataState<Nothing>>()
    val AddToCortProducts: LiveData<DataState<Nothing>> = _AddToCartProducts

    private val _fetchCartProducts = MutableLiveData<DataState<List<ProductModel>>>()
    val fetchCartProducts: LiveData<DataState<List<ProductModel>>> = _fetchCartProducts

    private val _AddUserToUserChatList = MutableLiveData<DataState<Nothing>>()
    val AddUserToUserChatList: LiveData<DataState<Nothing>> = _AddUserToUserChatList

    private val _fetchUserToUserChatList = MutableLiveData<DataState<List<ProductModel>>>()
    val fetchUserToUserChatList: LiveData<DataState<List<ProductModel>>> = _fetchUserToUserChatList

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

    fun uploadUpdatedProfilePhoto(uri: Uri, oldImageUri: Uri) {
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
                            val imageName = getImageNameFromUri(downloadedUri)
                            val oldImageName = getImageNameFromUri(oldImageUri)
                            updateProfilePhoto(downloadedUri.toString(), imageName, oldImageName)
                        }
                    }
                } else {
                    _updateStatus.value = DataState.Error(it.exception?.message!!)
                }
            }
    }

    private fun getImageNameFromUri(uri: Uri): String {
        val path = uri.path
        return path?.substring(path.lastIndexOf('/') + 1) ?: ""
    }

    private fun updateProfilePhoto(uri: String, imageName: String, oldImageName: String) {
        db.collection("users").document(uid)
            .update("profileImageUri", uri)
            .addOnSuccessListener {

                db.collection("users").document(uid)
                    .update("imageName", imageName)
                    .addOnSuccessListener {
                        updateUserPref(uri, imageName)
                        deleteOldProfileImage(oldImageName)
                        _profilePicUpdateStatus.value = DataState.Success()
                    }
                    .addOnFailureListener {
                        _profilePicUpdateStatus.value = DataState.Error(it.message!!)
                    }
            }
            .addOnFailureListener {
                _profilePicUpdateStatus.value = DataState.Error(it.message!!)
            }

    }

    private fun deleteOldProfileImage(imageName: String) {
        // Get reference to the old profile image
        val oldProfileImageRef = storage.child("profile_images").child(imageName)

        // Delete the old profile image
        oldProfileImageRef.delete()
            .addOnSuccessListener {
                // Handle successful deletion
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e("Exception", "Error deleting old profile image: $exception")
            }
    }

    private fun updateUserPref(uri: String, imageName: String) {
        val user = prefs.getUserData()
        if (user != null) {
            user.profileImageUri = uri
            user.imageName = imageName
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
                    product.productImageName = getImageNameFromUri(uri1)
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
                        product.productImageName =
                            getImageNameFromUri(product.productImgeUri.toUri())
                        productsList.add(product)
                    }
                }

                _fetchProducts.value = DataState.Success(productsList)
            }
    }


//    fun fetchProductListings() {
//        _fetchProductsListings.value = DataState.Loading
//        db.collectionGroup("products")
//            .addSnapshotListener { value, error ->
//                if (error != null) {
//                    _fetchProductsListings.value = DataState.Error(error.message!!)
//                    return@addSnapshotListener
//                }
//
//                val productsList = mutableListOf<ProductModel>()
//                for (data in value?.documents!!) {
//                    val product = data.toObject(ProductModel::class.java)
//                    if (product != null) {
//                        val docId = data.id
//                        product.docId = docId
//                        productsList.add(product)
//                    }
//                }
//
//                _fetchProductsListings.value = DataState.Success(productsList)
//            }
//    }

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
                                    product.productImageName =
                                        getImageNameFromUri(product.productImgeUri.toUri())
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


    fun uploadUpdateProductImage(uri: Uri, oldImageUri: Uri, product: ProductModel) {
        _porductPicUpdateStatus.value = DataState.Loading
        val ref = storage.child("Product_images/${System.currentTimeMillis()}")

        // Convert Uri to Bitmap
        val bitmap = ImageUtils.uriToBitmap(context, uri)

        // Compress bitmap to JPEG format
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()
        var imageName: String = ""

        // Upload the new image
        ref.putBytes(data)
            .addOnCompleteListener { uploadTask ->
                if (uploadTask.isSuccessful) {
                    // Get the download URL for the newly uploaded image
                    ref.downloadUrl.addOnSuccessListener { downloadedUri ->
                        if (downloadedUri != null) {
                            // Extract image name from the URL
                            imageName = getImageNameFromUri(downloadedUri)

                            // Update profile photo URL in the database

                            imageNameUri = getImageNameFromUri(oldImageUri)

                            updateProductPhoto(
                                downloadedUri.toString(),
                                imageName,
                                imageNameUri,
                                product
                            )

                        }
                    }
                } else {
                    // Handle upload failure
                    _porductPicUpdateStatus.value = DataState.Error(uploadTask.exception?.message!!)
                }
            }
            .addOnSuccessListener {
                // Delete the old profile image after successful upload of the new image
                _porductPicUpdateStatus.value = DataState.Success()
            }
    }

    private fun updateProductPhoto(
        uri: String,
        imageName: String,
        oldImageUri: String,
        product: ProductModel
    ) {

//        db.collection("users_products").document(uid).collection("products").document(product.docId)
//            .addSnapshotListener { value, error ->
//
//                val productfetched = value?.toObject(ProductModel::class.java)
//                if (productfetched != null) {
//                    val imageUri=productfetched.productImgeUri
//                    imageNameUri=getImageNameFromUri(imageUri.toUri())
//                }
        db.collection("users_products").document(uid).collection("products")
            .document(product.docId)
            .update("productImgeUri", uri)
            .addOnSuccessListener {

                db.collection("users_products").document(uid).collection("products")
                    .document(product.docId)
                    .update("productImageName", imageName)
                    .addOnSuccessListener {

                        deleteOldProductImage(oldImageUri)
                    }
                    .addOnFailureListener {
                        _porductPicUpdateStatus.value = DataState.Error(it.message!!)
                    }
            }
            .addOnFailureListener {
                _porductPicUpdateStatus.value = DataState.Error(it.message!!)
            }
    }

    private fun deleteOldProductImage(imageName: String) {
        // Get reference to the old profile image
        val oldProfileImageRef = storage.child("Product_images").child(imageName)

        // Delete the old profile image
        oldProfileImageRef.delete()
            .addOnSuccessListener {
                // Handle successful deletion
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e("Exception", "Error deleting old profile image: $exception")
            }
    }

    fun updateProduct(product: ProductModel) {
        _updateProductStatus.value = DataState.Loading
        db.collection("users_products").document(uid).collection("products").document(product.docId)
            .set(product)
            .addOnSuccessListener {
                _updateProductStatus.value = DataState.Success()
            }
            .addOnFailureListener {
                _updateProductStatus.value = DataState.Error(it.message!!)
            }
    }

    fun addProductToCart(cartModel: CartModel) {
        _AddToCartProducts.value = DataState.Loading
        val cartRef = db.collection("products_at_cart").document(uid).collection("products").document(cartModel.docId_ofProduct)
        cartRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Product already in cart, update quantity or show error message
                _AddToCartProducts.value = DataState.Error("Already Added to Cart")
                //_AddToCartProducts.value = DataState.Success() // Product already in cart
            } else {
                cartRef.set(cartModel)
                    .addOnSuccessListener {
                        _AddToCartProducts.value = DataState.Success()
                    }
                    .addOnFailureListener { exception ->
                        _AddToCartProducts.value = DataState.Error(exception.message!!)
                    }
            }
        }
    }





    fun fetchProductsOfCart() {
        _fetchCartProducts.value = DataState.Loading

        db.collection("products_at_cart").document(uid).collection("products")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    _fetchCartProducts.value = DataState.Success(emptyList())
                    return@addOnSuccessListener
                }

                val tasks = mutableListOf<Task<DocumentSnapshot>>()
                val productsList = mutableListOf<ProductModel>()

                for (data in snapshot.documents) {
                    val cartItem = data.toObject(CartModel::class.java)
                    cartItem?.let {
                        val cartItem_id=data.id
                        val task = db.collection("users_products")
                            .document(cartItem.user_ID_ofProduct)
                            .collection("products")
                            .document(cartItem.docId_ofProduct)
                            .get()

                        tasks.add(task)

                        task.addOnSuccessListener { documentSnapshot ->
                            val product = documentSnapshot.toObject(ProductModel::class.java)
                            product?.let { p ->
                                p.docId = cartItem.docId_ofProduct
                                p.user_ID = cartItem.user_ID_ofProduct
                                p.cartItemID=cartItem_id

                                // Fetch user data
                                val userTask = db.collection("users")
                                    .document(cartItem.user_ID_ofProduct)
                                    .get()

                                tasks.add(userTask)

                                userTask.addOnSuccessListener { userDocument ->
                                    val user = userDocument.toObject(ModelUser::class.java)
                                    user?.let { u ->
                                        val userName = u.fullName
                                        p.productSellerName = userName
                                        p.productImageName = getImageNameFromUri(p.productImgeUri.toUri())
                                        productsList.add(p)

                                        // Check if all tasks are complete
                                        if (tasks.all { it.isComplete }) {
                                            // All tasks are complete, update the state
                                            _fetchCartProducts.value = DataState.Success(productsList)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { error ->
                _fetchCartProducts.value = DataState.Error(error.message ?: "Unknown error occurred")
            }
    }


    fun addUserToUserChatList(chatUserListDataModel: ChatUserListDataModel) {
        _AddUserToUserChatList.value = DataState.Loading
        val userChatListRef = db.collection("Users_At_Chat").document(uid).collection("User").document(chatUserListDataModel.UserUid)
        userChatListRef.set(chatUserListDataModel)
            .addOnSuccessListener {
                _AddUserToUserChatList.value = DataState.Success()
            }
            .addOnFailureListener { exception ->
                _AddUserToUserChatList.value = DataState.Error(exception.message!!)
            }
    }



}
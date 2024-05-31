package com.example.agrilinkup.View.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.agrilinkup.ModelUser
import com.example.agrilinkup.Models.Entities.CartModel
import com.example.agrilinkup.Models.Entities.ProductModel
import com.example.agrilinkup.Models.Entities.ProductSharedPreferance
import com.example.agrilinkup.Models.Entities.messages.ChatUserListDataModel
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.R
import com.example.agrilinkup.View.VmProfile
import com.example.agrilinkup.databinding.ProductListingItemsBinding
import com.example.agrilinkup.ui.ProfileRepository
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.ProgressDialogUtil
import com.example.agrilinkup.utils.ProgressDialogUtil.dismissProgressDialog
import com.example.agrilinkup.utils.ProgressDialogUtil.showProgressDialog
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class AdapterProductsListings(private val items: List<ProductModel>,
                              val context: Context,
                              val navigator: NavController,
                              val lifecycleOwner12: LifecycleOwner,
                              val  fragmentManager12: FragmentManager) :
    RecyclerView.Adapter<AdapterProductsListings.ViewHolder>() {

    @Inject
    lateinit var preferenceManager: ProductSharedPreferance
    lateinit var prefs:PreferenceManager
    private lateinit var vmProfile: VmProfile
    val db = Firebase.firestore
    val auth = Firebase.auth
    //val preferenceManager=PreferenceManager(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProductListingItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]

        holder.bind(data)
    }

    inner class ViewHolder(val binding: ProductListingItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
            val imageview=binding.imgItemProductThumb

        fun bind(product: ProductModel) {

            binding.txtItemProductSeller.text = product.productSellerName
            binding.txtItemProductName.text = product.productTitle
            binding.txtItemProductLocation.text = product.UserLocation
            binding.txtItemProductPrice.text = product.pricePerUnit
            Glide.with(context)
                .load(product.productImgeUri)
                .into(binding.imgItemProductThumb)
            binding.AvailableStatus.text=product.productStatus

            binding.imgItemProductThumb.setOnClickListener(View.OnClickListener {
                val builder= AlertDialog.Builder(context,R.style.CustomAlertDialog).create()
                val inflaterRaw=LayoutInflater.from(context)
                val view= inflaterRaw.inflate(R.layout.custom_product_image_view_layout,null)
                val text=view.findViewById<TextView>(R.id.productNameTxtView)
                text.text=product.productTitle
                val  image=view.findViewById<ImageView>(R.id.productImageview)
                Glide.with(context)
                    .load(product.productImgeUri)
                    .into(image)
                val btnCart=view.findViewById<Button>(R.id.button2)
                btnCart.setOnClickListener{
                    addToCart(product)
                }
                val chatBtn=view.findViewById<Button>(R.id.chatBtnGo)
                chatBtn.setOnClickListener{
                    addChatUserAtUserChatList(product)
                    builder.dismiss()
                }
                builder.setView(view)
                builder.setCanceledOnTouchOutside(true)
                builder.show()
            })

            binding.onItmeClickLayout.setOnClickListener(View.OnClickListener {
                val preferenceManager=ProductSharedPreferance(context)
                preferenceManager.saveTempProduct(product)
                navigator.navigate(R.id.action_mainFragment2_to_productDetailsFragment)
            })

            binding.addToCart.setOnClickListener{
                addToCart(product)
            }
            binding.gotoInbox.setOnClickListener {
                addChatUserAtUserChatList(product)
            }

//            deleteProduct(product.docId)
//            updateProduct(product.docId)
        }

        private fun addChatUserAtUserChatList(product: ProductModel){
            db.collection("users").document(product.user_ID)
                .get().addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(ModelUser::class.java)
                    if (user != null) {
                        val docId = documentSnapshot.id
                        user.docId = docId
                        addItemChat(product,user)
                    }
                }.addOnFailureListener {
                    Toast.makeText(context,it.message!!,Toast.LENGTH_SHORT).show()
                }
        }
        private fun addItemChat(product: ProductModel,user: ModelUser){
            val prefs= PreferenceManager(context)
            val auth= FirebaseAuth.getInstance()
            val db= FirebaseFirestore.getInstance()
            val storage= FirebaseStorage.getInstance().getReference()
            val context=context

            val profileRepo= ProfileRepository(db, auth, prefs, storage, context)
            vmProfile = VmProfile(profileRepo)
            val UserData=ChatUserListDataModel(
                user.fullName,
                user.profileImageUri,
                product.user_ID,
                product.docId
            )
            val CurrentUser = prefs.getUserData()
            val senderUserData = ChatUserListDataModel(
                CurrentUser?.fullName!!,
                CurrentUser.profileImageUri,
                CurrentUser.docId,
                product.docId
            )

            vmProfile.addUserToUserChatList(UserData,senderUserData)
            userAddObserver()
            val bundle = Bundle()
            bundle.apply {
                putString("userName",user.fullName)
                putString("receiverUid",product.user_ID)
                putString("profileImageLink",user.profileImageUri)
            }
            navigator.navigate(R.id.action_mainFragment2_to_messagesChatFragment,bundle)
        }

        private fun userAddObserver() {
            vmProfile.AddUserToUserChatList.observe(lifecycleOwner12) {
                when (it) {
                    is DataState.Success -> {
                        //Toast.makeText(context,"User added to chatList",Toast.LENGTH_SHORT).show()
                        dismissProgressDialog()
                    }

                    is DataState.Error -> {
                        //Toast.makeText(context,it.errorMessage,Toast.LENGTH_SHORT).show()
                        dismissProgressDialog()
                    }

                    is DataState.Loading -> {
                        showProgressDialog(fragmentManager12)
                    }

                }
            }
        }
        private fun addToCart(product: ProductModel){
            val prefs= PreferenceManager(context)
            val auth= FirebaseAuth.getInstance()
            val db= FirebaseFirestore.getInstance()
            val storage= FirebaseStorage.getInstance().getReference()
            val context=context

            val profileRepo= ProfileRepository(db, auth, prefs, storage, context)
            vmProfile = VmProfile(profileRepo)
            val cartItem=CartModel(
                product.user_ID,
                product.docId
            )
            vmProfile.addProductToCart(cartItem)
            setUpObserver()
        }


        private fun setUpObserver() {
            vmProfile.AddToCortProducts.observe(lifecycleOwner12) {
                when (it) {
                    is DataState.Success -> {
                        Toast.makeText(context,"Added to Cart",Toast.LENGTH_SHORT).show()
                        dismissProgressDialog()
                    }

                    is DataState.Error -> {
                        Toast.makeText(context,it.errorMessage,Toast.LENGTH_SHORT).show()
                        ProgressDialogUtil.dismissProgressDialog()
                    }

                    is DataState.Loading -> {
                        ProgressDialogUtil.showProgressDialog(fragmentManager12)
                    }

                }
            }
        }



//        private fun deleteProduct(docId: String) {
//            binding.deleteProductListing.setOnClickListener {
//                db.collection("users_products")
//                    .document(auth.uid!!)
//                    .collection("products")
//                    .document(docId)
//                    .delete()
//            }
//        }
//
//        private fun updateProduct(docId: String){
//            binding.updateProductListing.setOnClickListener{
//                findNavController(UpdateMyProductFragment()).navigate(R.id.action_userProductsFragment2_to_updateMyProductFragment)
//            }
//        }
    }

}
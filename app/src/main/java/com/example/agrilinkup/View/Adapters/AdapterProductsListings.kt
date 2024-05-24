package com.example.agrilinkup.View.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.agrilinkup.Models.Entities.ProductModel
import com.example.agrilinkup.Models.Entities.ProductSharedPreferance
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.R
import com.example.agrilinkup.View.Fragments.UserAccount.UpdateMyProductFragment
import com.example.agrilinkup.databinding.ItemMyProductListingsBinding
import com.example.agrilinkup.databinding.ProductListingItemsBinding
import com.example.agrilinkup.utils.gone
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlin.properties.Delegates

class AdapterProductsListings(private val items: List<ProductModel>,val context: Context,val navigator: NavController) :
    RecyclerView.Adapter<AdapterProductsListings.ViewHolder>() {

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
                builder.setView(view)
                builder.setCanceledOnTouchOutside(true)
                builder.show()
            })

            binding.onItmeClickLayout.setOnClickListener(View.OnClickListener {
                val preferenceManager=ProductSharedPreferance(context)
                preferenceManager.saveTempProduct(product)
                navigator.navigate(R.id.action_mainFragment2_to_productDetailsFragment)
            })

//            deleteProduct(product.docId)
//            updateProduct(product.docId)
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
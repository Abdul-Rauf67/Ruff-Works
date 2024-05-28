package com.example.agrilinkup.View.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.agrilinkup.CartFragment
import com.example.agrilinkup.ChatFragment
import com.example.agrilinkup.Models.Entities.ProductModel
import com.example.agrilinkup.Models.Entities.ProductSharedPreferance
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.R
import com.example.agrilinkup.View.Fragments.MainFragment
import com.example.agrilinkup.databinding.ProductListingItemsAtCartBinding
import com.example.agrilinkup.utils.invisible
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class AdapterAtCartRecyclerView(private val items: List<ProductModel>,val context: Context,val navigator: NavController) :
    RecyclerView.Adapter<AdapterAtCartRecyclerView.ViewHolder>() {

    val db = Firebase.firestore
    val auth = Firebase.auth
    val preferenceManager=PreferenceManager(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProductListingItemsAtCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]
        holder.bind(data)
    }

    inner class ViewHolder(val binding: ProductListingItemsAtCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductModel) {
            binding.txtItemProductSeller.text = product.productSellerName
            binding.txtItemProductName.text = product.productTitle
            //binding.txtItemProductSeller.text = product.user_ID
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
                val layoutRemove=view.findViewById<LinearLayout>(R.id.layour12)
                layoutRemove.visibility=View.INVISIBLE
//                val btnCart=view.findViewById<Button>(R.id.button2)
//                btnCart.text="Delete From Cart"
//                btnCart.setOnClickListener(View.OnClickListener {
//                    deleteProduct(product)
//                })
                builder.setView(view)
                builder.setCanceledOnTouchOutside(true)
                builder.show()
            })

            binding.onItmeClickLayout.setOnClickListener(View.OnClickListener {
                val preferenceManager=ProductSharedPreferance(context)
                product.tempValue="CartFragment"
                preferenceManager.saveTempProduct(product)
                navigator.navigate(R.id.action_mainFragment2_to_productDetailsFragment)
            })

            deleteProduct(product)
        }


        private fun deleteProduct(product: ProductModel) {

            binding.deleteProductFromCart.setOnClickListener {
                db.collection("products_at_cart")
                    .document(auth.uid!!)
                    .collection("products")
                    .document(product.cartItemID)
                    .delete()
            }
        }

    }

}
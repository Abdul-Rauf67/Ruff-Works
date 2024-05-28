package com.example.agrilinkup.View.Fragments.HomeProduct

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.agrilinkup.Models.Entities.CartModel
import com.example.agrilinkup.Models.Entities.ProductModel
import com.example.agrilinkup.Models.Entities.ProductSharedPreferance
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.View.VmProfile
import com.example.agrilinkup.databinding.FragmentProductDetailsBinding
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentProductDetailsBinding
    lateinit var product:ProductModel
    private lateinit var vmProfile: VmProfile
    val db = Firebase.firestore
    val auth = Firebase.auth
    lateinit var preferenceManager: ProductSharedPreferance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        preferenceManager=ProductSharedPreferance(requireContext())
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        product= preferenceManager.getProduct()!!


        if (product!=null) {
            val productName =
                product.productTitle + "  " + product.productSubTitle + "   " + product.productQuality


            Glide.with(requireContext())
                .load(product.productImgeUri)
                .into(binding.imgSellProduct)

            binding.productName.text = productName
            binding.location.text = product.UserLocation
            val priceAndQuantity = "RS: " + product.pricePerUnit + " per " + product.productQuantity+ " KG"
            binding.priceAndQuantity.text = priceAndQuantity
            binding.AvailableItemsOrUnits.text = product.productTotalUnits
            binding.discription.text = product.productDiscription
            binding.status.text = product.productStatus

            if (product.tempValue.equals("CartFragment")){
                goToCartOperations()
            }else{
                goToProductListingOperations()
            }

        }
    }


    private fun goToCartOperations(){
        binding.button2.text="Delete From Cart"
        binding.button2.setOnClickListener{
            db.collection("products_at_cart")
                .document(auth.uid!!)
                .collection("products")
                .document(product.cartItemID)
                .delete()
            findNavController().popBackStack()
        }
    }

    private fun goToProductListingOperations(){
        binding.button2.setOnClickListener{
            addToCart()
        }
    }
    private fun addToCart(){
        val prefs= PreferenceManager(requireContext())
        val auth= FirebaseAuth.getInstance()
        val db= FirebaseFirestore.getInstance()
        val storage= FirebaseStorage.getInstance().getReference()

        val profileRepo= ProfileRepository(db, auth, prefs, storage, requireContext())
        vmProfile = VmProfile(profileRepo)
        val cartItem= CartModel(
            product.user_ID,
            product.docId
        )
        vmProfile.addProductToCart(cartItem)
        findNavController().popBackStack()
     //   setUpObserver()
    }
    private fun setUpObserver() {
        vmProfile.AddToCortProducts.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    Toast.makeText(context,"Added to Cart", Toast.LENGTH_SHORT).show()
                    dismissProgressDialog()
                    findNavController().popBackStack()
                }

                is DataState.Error -> {
                    Toast.makeText(context,it.errorMessage, Toast.LENGTH_SHORT).show()
                    dismissProgressDialog()
                }

                is DataState.Loading -> {
                    showProgressDialog()
                }

            }
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProductDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }
}
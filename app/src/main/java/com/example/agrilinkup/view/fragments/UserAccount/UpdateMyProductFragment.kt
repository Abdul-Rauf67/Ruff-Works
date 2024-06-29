package com.example.agrilinkup.view.fragments.UserAccount

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.example.agrilinkup.Models.Entities.ProductModel
import com.example.agrilinkup.Models.Entities.ProductSharedPreferance
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.view.VmProfile
import com.example.agrilinkup.databinding.FragmentUpdateMyProductBinding
import com.example.agrilinkup.ui.ProfileRepository
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.Glide
import com.example.agrilinkup.utils.ProgressDialogUtil.dismissProgressDialog
import com.example.agrilinkup.utils.ProgressDialogUtil.showProgressDialog
import com.example.agrilinkup.utils.gone
import com.example.agrilinkup.utils.ifEmpty
import com.example.agrilinkup.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UpdateMyProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UpdateMyProductFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentUpdateMyProductBinding

    //Pref
    @Inject
    lateinit var preferenceManager: ProductSharedPreferance

    lateinit var product:ProductModel

    //Views
    private lateinit var udProductImage: String
    private lateinit var udProductTitle:EditText
    private lateinit var udProductSubTitile: EditText
    private lateinit var udProductQuality: EditText
    private lateinit var udProductLocation:EditText
    private lateinit var udPricePerUnit:EditText
    private lateinit var udProductQuantity:EditText
    private lateinit var udProductTotalUnits: EditText
    private lateinit var udProductDiscription: EditText
    private lateinit var udProductStatus:EditText
    private lateinit var udProductSeasonStartDate: EditText
    private lateinit var udProductSeasonEndDate: EditText
    private lateinit var userID:String
    private lateinit var docId:String
    private lateinit var oldProductImgUri: Uri
    private lateinit var newImageUri: Uri

    private lateinit var vmProfile: VmProfile

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
        binding = FragmentUpdateMyProductBinding.inflate(inflater, container, false)
        val prefs=PreferenceManager(requireContext())
        val auth= FirebaseAuth.getInstance()
        val db= FirebaseFirestore.getInstance()
        val storage= FirebaseStorage.getInstance().getReference()
        val context=requireContext()
        val profileRepository= ProfileRepository(db,auth,prefs,storage,context)
        vmProfile=VmProfile(profileRepository)
        preferenceManager= ProductSharedPreferance(requireContext())

        inIt()

        return binding.root
    }


    private fun inIt() {
        inItViews()
        getProductFromPrefAndSet()
        setOnClickListener()
        setUpObserver()
    }

    private fun inItViews() {
        udProductTitle=binding.txtSellProductName
        udProductSubTitile=binding.txtProductSubType
        udProductQuality=binding.productQuality
        udProductLocation=binding.txtSellLocation
        udPricePerUnit=binding.txtSellPrice
        udProductQuantity=binding.txtSellQuantity
        udProductTotalUnits=binding.txtSellUnit
        udProductDiscription=binding.txtSellDiscription
        udProductStatus=binding.txtSellStatus
        udProductSeasonStartDate=binding.seasonStart
        udProductSeasonEndDate=binding.seasonEnd

    }

    private fun setOnClickListener() {

        binding.addProductImage.setOnClickListener {
            setUpImagePicker()
            if (isImageChanged) {
                vmProfile.uploadUpdateProductImage(newImageUri, oldProductImgUri,product)
            }
        }

        binding.btnUpdate.setOnClickListener {
            if (validateForm()) {
                updateProduct()
            }
        }
    }

//    private val pickMedia =
//        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
//            if (uri != null) {
//                binding.addProductImage.setImageURI(uri)
//                vmProfile.updatePic(uri)
//            } else {
//                Log.d("PhotoPicker", "No media selected")
//            }
//        }
//    private fun openPhotoPicker() {
//        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//    }

    private fun updateProduct() {

        val productTitile = udProductTitle.text.toString().trim()
        val productSubTitle = udProductSubTitile.text.toString().trim()
        val productQuality=udProductQuality.text.toString().trim()
        val productUserLocation=udProductLocation.text.toString().trim()
        val priceOfUnit = udPricePerUnit.text.toString().trim()
        val productQuantity = udProductQuantity.text.toString().trim()
        val AvailableUnits = udProductTotalUnits.text.toString().trim()
        val productDiscription=udProductDiscription.text.toString().trim()
        val productAvailStatus=udProductStatus.text.toString().trim()
        val SeasonStartDate = udProductSeasonStartDate.text.toString().trim()
        val SeasonEndDate=udProductSeasonEndDate.text.toString().trim()

        val updatedproduct = ProductModel(
            newImageUri.toString(),
            productTitile,
            productSubTitle,
            productQuality,
            productUserLocation,
            priceOfUnit,
            productQuantity,
            AvailableUnits,
            productDiscription,
            productAvailStatus,
            SeasonStartDate,
            SeasonEndDate,
            userID,
            docId
        )
        vmProfile.updateProduct(updatedproduct)
    }

    private fun getProductFromPrefAndSet() {
            product = preferenceManager.getProduct()!!
        product?.let {
            udProductTitle.setText(it.productTitle)
            udProductSubTitile.setText(it.productSubTitle)
            udProductQuality.setText(it.productQuality)
            udProductLocation.setText(it.UserLocation)
            udPricePerUnit.setText(it.pricePerUnit)
            //this.udProductImage = it.productImgeUri
            udProductQuantity.setText(it.productQuantity)
            udProductTotalUnits.setText(it.productTotalUnits)
            udProductDiscription.setText(it.productDiscription)
            udProductStatus.setText(it.productStatus)
            udProductSeasonStartDate.setText(it.productSeasonStartDate)
            udProductSeasonEndDate.setText(it.productseasonEndDate)
            udProductImage=it.productImgeUri
            userID=it.user_ID
            docId=it.docId
            oldProductImgUri=it.productImgeUri.toUri()

            Glide.loadImageWithListener(
                requireContext(),
                it.productImgeUri,
                binding.imgSellProduct
            ) {
                binding.pgProfileImage.gone()
            }
        }
    }

    private fun setUpObserver() {
        vmProfile.updateProductStatus.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    toast("Updated")
                    findNavController().popBackStack()
                    dismissProgressDialog()
                }

                is DataState.Error -> {
                    toast(it.errorMessage)
                    dismissProgressDialog()
                }

                is DataState.Loading -> {
                    showProgressDialog()
                }
            }
        }

    }

    private fun validateForm(): Boolean {

        val productTitle = binding.txtSellProductName
        val productSubTitle = binding.txtProductSubType
        val productQuality = binding.productQuality
        val UserLocation = binding.txtSellLocation
        val pricePerUnit = binding.txtSellPrice
        val productQuantity = binding.txtSellQuantity
        val productTotalUnits = binding.txtSellUnit
        val productDiscription = binding.txtSellDiscription
        val productStatus = binding.txtSellStatus
        val productSeasonStartDate = binding.seasonStart
        val productseasonEndDate = binding.seasonEnd

        productTitle.error = null
        productSubTitle.error = null
        productQuality.error = null
        UserLocation.error = null
        pricePerUnit.error = null
        productQuantity.error = null
        productTotalUnits.error = null
        productDiscription.error = null
        productStatus.error = null
        productSeasonStartDate.error=null
        productseasonEndDate.error=null

        return when {
            productTitle.ifEmpty("Product name is Required") -> false
            productSubTitle.ifEmpty("Product SubType  is required") -> false
            productQuality.ifEmpty("product Quality Required") -> false
            UserLocation.ifEmpty("Your Address is Required") -> false
            pricePerUnit.ifEmpty("PricePerUnit is required") -> false
            productQuantity.ifEmpty("Enter product Quantity Such as 40KG, 50KG, etc.") -> false
            productTotalUnits.ifEmpty("Type the Total number of available Units/items") -> false
            productDiscription.ifEmpty("Product Discription is required") -> false
            productSeasonStartDate.ifEmpty("Select product selling season starting date") -> false
            productseasonEndDate.ifEmpty("Select product selling season ending date") -> false
            productStatus.ifEmpty("Type your product Status Such as Available, OutOfStack,etc.") -> false

            else -> {
                true
            }
        }
    }

    private var isImageChanged = false
    private val launcherForImagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val uri = data?.data
                    if (uri != null) {
                        newImageUri = uri
                        binding.imgSellProduct.setImageURI(data.data)
                        isImageChanged = true
                    }
                }

                com.github.dhaval2404.imagepicker.ImagePicker.RESULT_ERROR -> {
                    toast(com.github.dhaval2404.imagepicker.ImagePicker.getError(data))
                }

                else -> {
                    toast("Task Cancelled")
                }
            }
        }

    private fun setUpImagePicker() {
        com.github.dhaval2404.imagepicker.ImagePicker.with(this)
            .crop()
            .compress(500)
            .createIntent {
                launcherForImagePicker.launch(it)
            }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UpdateMyProductFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UpdateMyProductFragment().apply {
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
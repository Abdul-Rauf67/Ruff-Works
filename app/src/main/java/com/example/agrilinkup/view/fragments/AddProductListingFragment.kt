package com.example.agrilinkup.view.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.agrilinkup.Models.Entities.ProductModel
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.view.VmProfile
import com.example.agrilinkup.databinding.FragmentAddProductListingBinding
import com.example.agrilinkup.ui.ProfileRepository
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.KeyboardUtils.hideKeyboard
import com.example.agrilinkup.utils.ProgressDialogUtil
import com.example.agrilinkup.utils.ifEmpty
import com.example.agrilinkup.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddProductListingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddProductListingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentAddProductListingBinding
    private lateinit var profileImgUri: Uri


    @Inject
    lateinit var preferenceManager: PreferenceManager
    private lateinit var auth: FirebaseAuth
    lateinit var productCategoryType:String
    private lateinit var vmProfile: VmProfile
    var notSelectedproductCategory=false

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
        // return inflater.inflate(R.layout.fragment_add_product_listing, container, false)

        binding = FragmentAddProductListingBinding.inflate(inflater, container, false)
        val prefs = PreferenceManager(requireContext())
        auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance().getReference()
        val context = requireContext()
        val profileRepository = ProfileRepository(db, auth, prefs, storage, context)
        vmProfile = VmProfile(profileRepository)

        preferenceManager = PreferenceManager(requireContext())

        inIt()

        return binding.root

    }

    private fun inIt() {
        inIt1()
        setOnClickListener()
        setUpObserver()
    }

    private fun inIt1() {

        val inputProductcategories = arrayOf(
            "Select Product Category",
            "Grains - اناج" to "Anaj",
            "Fruits - پھل" to "Phal",
            "Vegetables - سبزیاں" to "Sabziyan",
            "Legumes - دالیں " to "Daloon",
            "Nuts and seeds - نٹس اور بیج" to "Nuts aur Beejon",
            "Dairy products - دودھ کے پروڈکٹس" to "Doodh ke Products",
            "Meat and poultry - گوشت اور پولیٹری" to "Gosht aur Poultry",
            "Eggs - انڈے" to "Anday",
            "Fiber crops - فائبر کپاس" to "Fiber Kapas",
            "Spices and herbs - مصالحے اور جڑی بوٹیاں" to "Masalay aur Jari Botiyan",
            "Beverages - مشروبات" to "Mashrobat",
            "Oils - تیل" to "Teel"
        )
        val saveProductCategories= arrayOf(
            "NotSelect",
            "Grains", "Fruits", "Vegetables",
            "Legumes", "Nuts__Seeds", "Dairy_products",
            "Meat__Poultry", "Eggs","Fiber_crops",
            "Spices_herbs","Beverages","Oils"
        )
        if (binding.productCategory!=null) {
            var adapter1= ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,inputProductcategories)
            binding.productCategory.adapter = adapter1
            1.also { binding.productCategory.id = it }
            binding.productCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long){
                    productCategoryType= saveProductCategories[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    notSelectedproductCategory=true
                }
            }
        }


        binding.seasonStart.setOnClickListener(View.OnClickListener {
            var cal = Calendar.getInstance()
            var dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                var month1 = month + 1
                var msg = "$dayOfMonth/$month1/$year"
                binding.seasonStart.setText(msg)
            }
            DatePickerDialog(
                requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        })
        binding.seasonEnd.setOnClickListener(View.OnClickListener {
            var cal = Calendar.getInstance()
            var dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                var month1 = month + 1
                var msg = "$dayOfMonth/$month1/$year"
                binding.seasonEnd.setText(msg)

            }
            DatePickerDialog(
                requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        })



        binding.cancel.setOnClickListener {
            findNavController().popBackStack()
            //findNavController().navigate(R.id.action_addProductListingFragment_to_homeFragment)
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddProductListingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddProductListingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
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
                        profileImgUri = uri
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

    fun setOnClickListener() {
        binding.btnSell.setOnClickListener {
            if (validateForm()) {
                hideKeyboard(it)
                createUserAccount()
            }
        }
        binding.addProductImage.setOnClickListener {
            setUpImagePicker()
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
        val productDiscription = binding.txtSellDescription
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

            notSelectedproductCategory -> {
                toast("Please Select Product Category!")
                false
            }

            productCategoryType.equals("NotSelect") ->{
                toast("Please Select Product Category!")
                false
            }



            else -> {
                true
            }
        }
    }

    private fun createUserAccount() {
        val productTitle = binding.txtSellProductName.text.toString()
        val productSubTitle = binding.txtProductSubType.text.toString()
        val productQuality = binding.productQuality.text.toString()
        val UserLocation = binding.txtSellLocation.text.toString()
        val pricePerUnit = binding.txtSellPrice.text.toString()
        val productQuantity = binding.txtSellQuantity.text.toString()
        val productTotalUnits = binding.txtSellUnit.text.toString()
        val productDiscription = binding.txtSellDescription.text.toString()
        val productStatus = binding.txtSellStatus.text.toString()
       // val productSeason = seasonIsSet
        val productSeasonStartDate = binding.seasonStart.text.toString()
        val productseasonEndDate = binding.seasonEnd.text.toString()
        val user_id = auth.currentUser?.uid!!


        val product = ProductModel(
            profileImgUri.toString(),
            productTitle,
            productSubTitle,
            productCategoryType,
            productQuality,
            UserLocation,
            pricePerUnit,
            productQuantity,
            productTotalUnits,
            productDiscription,
            productStatus,
            productSeasonStartDate,
            productseasonEndDate,
            user_id
        )

        lifecycleScope.launch(Dispatchers.IO) {
            vmProfile.addProduct(product)
        }
    }


    private fun setUpObserver() {
        vmProfile.addProductStatus.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    findNavController().popBackStack()
                }

                is DataState.Error -> {
                    toast(it.errorMessage)
                    ProgressDialogUtil.dismissProgressDialog()
                }

                is DataState.Loading -> {
                    ProgressDialogUtil.showProgressDialog(childFragmentManager)
                }

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }
}
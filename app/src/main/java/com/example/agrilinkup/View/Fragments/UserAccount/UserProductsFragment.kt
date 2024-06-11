package com.example.agrilinkup.View.Fragments.UserAccount

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.agrilinkup.Models.Entities.ProductModel
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.R
import com.example.agrilinkup.View.Adapters.AdapterMyProducts
import com.example.agrilinkup.View.Adapters.AdapterProductsListings
import com.example.agrilinkup.View.Fragments.AddProductListingFragment
import com.example.agrilinkup.View.VmProfile
import com.example.agrilinkup.databinding.FragmentUserProductsBinding
import com.example.agrilinkup.ui.ProfileRepository
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.ProgressDialogUtil.dismissProgressDialog
import com.example.agrilinkup.utils.ProgressDialogUtil.showProgressDialog
import com.example.agrilinkup.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Locale


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserProductsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserProductsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding:FragmentUserProductsBinding

    private lateinit var vmProfile: VmProfile
    private lateinit var preferenceManager:PreferenceManager
    lateinit var productsList:List<ProductModel>
    lateinit var product: ProductModel
    lateinit var adapter: AdapterMyProducts
     var productCategoryType:String=""
     var orderOfPrice:String=""
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
        binding = FragmentUserProductsBinding.inflate(inflater, container, false)

        val prefs= PreferenceManager(requireContext())
        val auth= FirebaseAuth.getInstance()
        val db= FirebaseFirestore.getInstance()
        val storage= FirebaseStorage.getInstance().getReference()
        val context=requireContext()
        preferenceManager=prefs

        val profileRepo= ProfileRepository(db, auth, prefs, storage, context)
        vmProfile = VmProfile(profileRepo)
        vmProfile.fetchProducts()
        setUpObserver()


        binding.txtSearchProducts.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase(Locale.ROOT)
                if (!productsList.isEmpty()) {
                    val filteredList = productsList.filter {
                        it.productTitle.lowercase(Locale.ROOT).contains(query)
                    }
                    if (adapter!=null) {
                        adapter.updateList(filteredList)
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })



        return binding.root

          }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val inputProductcategories = arrayOf(
            "Category",
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
        val filterProducts= arrayOf(
            "Filter Products",
            "Price: Low to High",
            "Price: High to Low",
            "Nearest"
        )
        if (binding.selectProductCategory!=null) {
            var adapter1= ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,inputProductcategories)
            binding.selectProductCategory.adapter = adapter1
            1.also { binding.selectProductCategory.id = it }
            binding.selectProductCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long){
                    productCategoryType= saveProductCategories[position]
                    when(position){
                        0 -> {
                            if(!orderOfPrice.isEmpty() && !orderOfPrice.equals("Filter Products") &&
                                !orderOfPrice.equals("Nearest")) {

                                if (orderOfPrice.equals("Price: Low to High")) {
                                    vmProfile.fetchProducts1("asc")
                                    setUpObserver()
                                }
                                else if (orderOfPrice.equals("Price: High to Low")) {
                                    vmProfile.fetchProducts1("desc")
                                    setUpObserver()
                                }
                            }
                        }
                        else -> {
                            if(!orderOfPrice.isEmpty() && !orderOfPrice.equals("Filter Products") &&
                                !orderOfPrice.equals("Nearest")) {

                                if (orderOfPrice.equals("Price: Low to High")) {
                                    vmProfile.fetchProducts(productCategoryType,"asc")
                                    setUpObserver()
                                }
                                else if (orderOfPrice.equals("Price: High to Low")) {
                                    vmProfile.fetchProducts(productCategoryType,"desc")
                                    setUpObserver()
                                }
                            }
                            else{
                                vmProfile.fetchProducts(productCategoryType)
                                setUpObserver()
                            }
                        }
                    }

                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    notSelectedproductCategory=true
                }
            }
        }

        if (binding.FilterProducts!=null) {
            var adapter2= ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,filterProducts)
            binding.FilterProducts.adapter = adapter2
            1.also { binding.FilterProducts.id = it }
            binding.FilterProducts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long){
                    orderOfPrice= filterProducts[position]
                    when(position){
                        1 -> {
                            if(!productCategoryType.isEmpty() && !productCategoryType.equals("NotSelect")) {
                                vmProfile.fetchProducts(productCategoryType, "asc")
                                setUpObserver()
                            }
                            else{
                                vmProfile.fetchProducts1("asc")
                                setUpObserver()
                            }
                        }
                        2 -> {
                            if(!productCategoryType.isEmpty() && !productCategoryType.equals("NotSelect")) {
                                vmProfile.fetchProducts(productCategoryType, "desc")
                                setUpObserver()
                            }
                            else {
                                vmProfile.fetchProducts1("desc")
                                setUpObserver()
                            }
                        }
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO()
                }
            }
        }

        binding.addProductsListings.setOnClickListener {
            findNavController().navigate(R.id.action_userProductsFragment2_to_addProductListingFragment3)
        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserProductsG.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserProductsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    private fun setUpObserver() {
        vmProfile.fetchProduct.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    productsList = it?.data!!
                    if (!productsList.isNullOrEmpty()) {
                        val navigator=findNavController()
                        adapter=AdapterMyProducts(productsList,requireContext(),navigator)

                        binding.recyclerViewProductItems.adapter =adapter
                      }
//                    else {
//                        binding.lyNoCars.visible()
//                        binding.rvVehicles.gone()
//                    }
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }
}

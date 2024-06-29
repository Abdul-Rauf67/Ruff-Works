package com.example.agrilinkup.view.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.agrilinkup.ChatFragment
import com.example.agrilinkup.Models.Entities.ProductModel
import com.example.agrilinkup.Models.Entities.ProductSharedPreferance
import com.example.agrilinkup.utils.Glide
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.R
import com.example.agrilinkup.view.activities.LoginActivity
import com.example.agrilinkup.view.Adapters.AdapterProductsListings
import com.example.agrilinkup.view.fragments.UserAccount.ProfileFragment
import com.example.agrilinkup.view.VmProfile
import com.example.agrilinkup.databinding.FragmentHomeBinding
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.Dialogs
import com.example.agrilinkup.utils.ProgressDialogUtil.dismissProgressDialog
import com.example.agrilinkup.utils.ProgressDialogUtil.showProgressDialog
import com.example.agrilinkup.utils.gone
import com.example.agrilinkup.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var preferenceManager: ProductSharedPreferance
    @Inject lateinit var prefs: PreferenceManager
    @Inject lateinit var db: FirebaseFirestore
    @Inject lateinit var storage: StorageReference


    //Navigation Header Views
    private lateinit var imageview1: ImageView
    private lateinit var progressOfImage: ProgressBar
    private lateinit var nameHeader: TextView
    private lateinit var emailHeader: TextView

    //Viewmodel
    private val vmProfile: VmProfile by viewModels()

    lateinit var productsList: List<ProductModel>
    lateinit var product: ProductModel
    lateinit var productAdapter: AdapterProductsListings
    var productCategoryType: String = ""
    var orderOfPrice: String = ""
    var notSelectedProductCategory = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        vmProfile.fetchProductsListings()
        setUpObserver()


        val navigation = binding.navView
        val header = navigation.getHeaderView(0)
        imageview1 = header.findViewById(R.id.navHeaderImageView)
        progressOfImage = header.findViewById(R.id.pgProfileImage)
        nameHeader = header.findViewById(R.id.UserName)
        emailHeader = header.findViewById(R.id.UserEmail)


        val toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        val toggleButton = ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerlsyout,
            toolbar,
            R.string.nav_open,
            R.string.nav_close
        )
        binding.drawerlsyout.addDrawerListener(toggleButton)
        toggleButton.syncState()


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
        val saveProductCategories = arrayOf(
            "NotSelect",
            "Grains", "Fruits", "Vegetables",
            "Legumes", "Nuts__Seeds", "Dairy_products",
            "Meat__Poultry", "Eggs", "Fiber_crops",
            "Spices_herbs", "Beverages", "Oils"
        )
        val filterProducts = arrayOf(
            "Filter Products",
            "Price: Low to High",
            "Price: High to Low",
            "Nearest"
        )
        if (binding.selectProductCategory != null) {
            val adapter1 = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                inputProductcategories
            )
            binding.selectProductCategory.adapter = adapter1
            1.also { binding.selectProductCategory.id = it }
            binding.selectProductCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        productCategoryType = saveProductCategories[position]

                        when (position) {
                            0 -> {
                                if (orderOfPrice.isNotEmpty() && orderOfPrice != "Filter Products" &&
                                    orderOfPrice != "Nearest"
                                ) {

                                    if (orderOfPrice == "Price: Low to High") {
                                        prefs.getUserData()?.let {
                                            vmProfile.fetchProductsListings1(
                                                it.docId,
                                                "asc"
                                            )
                                        }
                                        setUpObserver()
                                    } else if (orderOfPrice == "Price: High to Low") {
                                        prefs.getUserData()?.let {
                                            vmProfile.fetchProductsListings1(
                                                it.docId,
                                                "desc"
                                            )
                                        }
                                        setUpObserver()
                                    }
                                }
                            }

                            else -> {
                                if (orderOfPrice.isNotEmpty()) {

                                    if (orderOfPrice == "Price: Low to High") {
                                        prefs.getUserData()?.let {
                                            vmProfile.fetchProductsListings(
                                                it.docId,
                                                productCategoryType,
                                                "asc"
                                            )
                                        }

                                        setUpObserver()
                                    } else if (orderOfPrice == "Price: High to Low") {
                                        prefs.getUserData()?.let {
                                            vmProfile.fetchProductsListings(
                                                it.docId,
                                                productCategoryType,
                                                "desc"
                                            )
                                        }
                                        setUpObserver()
                                    }
                                } else {
                                    prefs.getUserData()?.let {
                                        vmProfile.fetchProductsListings(
                                            it.docId,
                                            productCategoryType
                                        )
                                    }
                                    setUpObserver()
                                }
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        notSelectedProductCategory = true
                    }
                }
        }

        if (binding.FilterProducts != null) {
            val adapter2 =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterProducts)
            binding.FilterProducts.adapter = adapter2
            1.also { binding.FilterProducts.id = it }
            binding.FilterProducts.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        orderOfPrice = filterProducts[position]
                        when (position) {
                            1 -> {
                                if (productCategoryType.isNotEmpty()) {
                                    if (productCategoryType == "NotSelect") {
                                        prefs.getUserData()?.let {
                                            vmProfile.fetchProductsListings(
                                                it.docId,
                                                productCategoryType,
                                                "asc"
                                            )
                                        }
                                        setUpObserver()
                                    } else {
                                        prefs.getUserData()?.let {
                                            vmProfile.fetchProductsListings1(
                                                it.docId,
                                                "asc"
                                            )
                                        }
                                        setUpObserver()
                                    }
                                } else {
                                    prefs.getUserData()
                                        ?.let { vmProfile.fetchProductsListings1(it.docId, "asc") }
                                    setUpObserver()
                                }
                            }

                            2 -> {
                                if (productCategoryType.isNotEmpty()) {
                                    if (productCategoryType != "NotSelect") {
                                        prefs.getUserData()?.let {
                                            vmProfile.fetchProductsListings(
                                                it.docId,
                                                productCategoryType,
                                                "desc"
                                            )
                                        }

                                        setUpObserver()
                                    } else {
                                        prefs.getUserData()?.let {
                                            vmProfile.fetchProductsListings1(
                                                it.docId,
                                                "desc"
                                            )
                                        }
                                        setUpObserver()
                                    }
                                } else {
                                    prefs.getUserData()
                                        ?.let { vmProfile.fetchProductsListings1(it.docId, "desc") }
                                    setUpObserver()
                                }
                            }
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        orderOfPrice = filterProducts[0]
                    }
                }
        }

        binding.txtSearchProducts.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase(Locale.ROOT)
                if (productsList.isNotEmpty()) {
                    val filteredList = productsList.filter {
                        it.productTitle.lowercase(Locale.ROOT).contains(query)
                    }
                    productAdapter.updateList(filteredList)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.navView.setNavigationItemSelectedListener { menuItem ->

            when (menuItem.itemId) {

                R.id.nav_profile ->
                    (parentFragment as MainFragment)
                        .replaceFragmentsInViewpager(ProfileFragment(), 3)

                R.id.chat_messages ->
                    (parentFragment as MainFragment)
                        .replaceFragmentsInViewpager(ChatMessagesFragment(), 1)

                R.id.chatsfragment123 ->
                    (parentFragment as MainFragment)
                        .replaceFragmentsInViewpager(ChatFragment(), 1)

                R.id.nav_logout ->
                    logout()

                else -> {
                    (parentFragment as MainFragment).switchToFragment(0)
                }
            }
        }
        setUpProfileImage()


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addProductsListings.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment2_to_addProductListingFragment3)
        }

//        binding.recyclerView.addOnItemTouchListener(
//            recyclerViewItemClickListener(requireContext(), binding.recyclerView,
//                object : recyclerViewItemClickListener.OnItemClickListener {
//                    override fun onItemClick(view: View, position: Int) {
//                        TODO("Not yet implemented")
//                    }
//
//                    override fun onItemLongClick(view: View?, position: Int) {
//                        val product=productsList[position]
//                        preferenceManager.saveTempProduct(product)
//                        findNavController().navigate(R.id.action_mainFragment2_to_productDetailsFragment)
//                    }
//                })
//        )
    }


//    al searchbar: EditText = view.findViewById(R.id.search_bar)
//    searchbar.addTextChangedListener(object : TextWatcher {
//        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//            val query = s.toString().toLowerCase()
//            val filteredList = productList.filter {
//                it.name.toLowerCase().contains(query)
//            }
//            adapter.updateList(filteredList)
//        }
//        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//        override fun afterTextChanged(s: Editable?) {}
//    })

    // Update the adapter's list

    private fun logout(): Boolean {
        Dialogs.logoutDialog(requireContext(), layoutInflater) {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
        return true
    }

    private fun setUpProfileImage() {
        val user = prefs.getUserData()
        user?.let {
            binding.toolbar.subtitle = it.fullName
            nameHeader.text = user.fullName
            emailHeader.text = user.email
            Glide.loadImageWithListener(requireContext(), user.profileImageUri, imageview1) {
                progressOfImage.gone()
            }
        }
    }

    private fun setUpObserver() {
        vmProfile.fetchProductsListings.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    productsList = it.data!!
                    if (productsList.isNotEmpty()) {
                        val navigater = findNavController()
                        productAdapter = AdapterProductsListings(
                            productsList, requireContext(),
                            navigater, viewLifecycleOwner, childFragmentManager
                        )
                        binding.recyclerView.adapter = productAdapter
                    }
//                    else {
//                        binding.lyNoCars.visible()
//                        binding.rvVehicles.gone()
//                    }
                    dismissProgressDialog()
                }

                is DataState.Error -> {
//                    binding.txtHomeError.visibility=View.VISIBLE
//                    binding.txtHomeError.text=it.errorMessage
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
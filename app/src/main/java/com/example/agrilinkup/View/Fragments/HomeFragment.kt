package com.example.agrilinkup

import android.content.Intent
import com.example.agrilinkup.utils.KeyboardUtils
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.example.agrilinkup.Models.Entities.ProductModel
import com.example.agrilinkup.Models.Entities.ProductSharedPreferance
import com.example.agrilinkup.Models.Entities.recyclerViewItemClickListener
import com.example.agrilinkup.utils.Glide
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.View.Activities.LoginActivity
import com.example.agrilinkup.View.Adapters.AdapterMyProducts
import com.example.agrilinkup.View.Adapters.AdapterProductsListings
import com.example.agrilinkup.View.Fragments.AddProductListingFragment
import com.example.agrilinkup.View.Fragments.Chat_Messages_Fragment
import com.example.agrilinkup.View.Fragments.HomeProduct.ProductDetailsFragment
import com.example.agrilinkup.View.Fragments.MainFragment
import com.example.agrilinkup.View.Fragments.UserAccount.ProfileFragment
import com.example.agrilinkup.View.VmProfile
import com.example.agrilinkup.databinding.FragmentHomeBinding
import com.example.agrilinkup.ui.ProfileRepository
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.Dialogs
import com.example.agrilinkup.utils.KeyboardUtils.hideKeyboard
import com.example.agrilinkup.utils.ProgressDialogUtil.dismissProgressDialog
import com.example.agrilinkup.utils.ProgressDialogUtil.showProgressDialog
import com.example.agrilinkup.utils.gone
import com.example.agrilinkup.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.Utils
import com.google.firebase.storage.FirebaseStorage
import java.util.Locale
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentHomeBinding
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var preferenceManager: ProductSharedPreferance
    lateinit var prefs:PreferenceManager

    //Navigation Header Views
    lateinit var  imageview1:ImageView
    lateinit var progressofImage:ProgressBar
    lateinit var nameHeader:TextView
    lateinit var emailHeader:TextView

    lateinit var vmProfile:VmProfile
    lateinit var productsList:List<ProductModel>
    lateinit var product:ProductModel
    lateinit var productAdapter:AdapterProductsListings
     var productCategoryType:String=""
     var orderOfPrice:String=""
    var notSelectedproductCategory=false
    var adapterPosition=0

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
        //val view=inflater.inflate(R.layout.fragment_home, container, false)

        binding= FragmentHomeBinding.inflate(inflater,container,false)


        auth= FirebaseModule_ProvideFirebaseAuthFactory.provideFirebaseAuth()
        //val view= binding.root
        prefs= PreferenceManager(requireContext())
        val auth= FirebaseAuth.getInstance()
        val db= FirebaseFirestore.getInstance()
        val storage= FirebaseStorage.getInstance().getReference()
        val context=requireContext()

        val profileRepo= ProfileRepository(db, auth, prefs, storage, context)
        vmProfile = VmProfile(profileRepo)
        prefs.getUserData()?.let { vmProfile.fetchProductsListings(it.docId) }
        setUpObserver()

        preferenceManager= ProductSharedPreferance(requireContext())


        var navigation=binding.navView
        var header=navigation.getHeaderView(0)
        imageview1=header.findViewById<ImageView>(R.id.navHeaderImageView)
        progressofImage=header.findViewById<ProgressBar>(R.id.pgProfileImage)
        nameHeader=header.findViewById(R.id.UserName)
        emailHeader=header.findViewById(R.id.UserEmail)
        //navImage=view.findViewById(R.id.navHeaderImageView)


        val toolbar=binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        val toggleButton=ActionBarDrawerToggle(
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
                                    prefs.getUserData()?.let { vmProfile.fetchProductsListings1(it.docId,"asc") }
                                    setUpObserver()
                                }
                                else if (orderOfPrice.equals("Price: High to Low")) {
                                    prefs.getUserData()?.let { vmProfile.fetchProductsListings1(it.docId,"desc") }
                                    setUpObserver()
                                }
                            }
                        }
                        else -> {
                            if(!orderOfPrice.isEmpty()) {

                                if (orderOfPrice.equals("Price: Low to High")) {
                                    prefs.getUserData()?.let { vmProfile.fetchProductsListings(it.docId,productCategoryType,"asc") }

                                    setUpObserver()
                                }
                                else if (orderOfPrice.equals("Price: High to Low")) {
                                    prefs.getUserData()?.let { vmProfile.fetchProductsListings(it.docId,productCategoryType,"desc") }
                                    setUpObserver()
                                }
                            }
                            else{
                                prefs.getUserData()?.let { vmProfile.fetchProductsListings(it.docId,productCategoryType) }
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
                            if(!productCategoryType.isEmpty()) {
                                if (productCategoryType.equals("NotSelect")) {
                                    prefs.getUserData()?.let { vmProfile.fetchProductsListings(it.docId,productCategoryType,"asc") }
                                    setUpObserver()
                                }
                                else {
                                    prefs.getUserData()?.let { vmProfile.fetchProductsListings1(it.docId,"asc") }
                                    setUpObserver()
                                }
                            }
                            else{
                                prefs.getUserData()?.let { vmProfile.fetchProductsListings1(it.docId,"asc") }
                                setUpObserver()
                            }
                        }
                        2 -> {
                            if(!productCategoryType.isEmpty()) {
                                if (!productCategoryType.equals("NotSelect")) {
                                    prefs.getUserData()?.let { vmProfile.fetchProductsListings(it.docId,productCategoryType,"desc") }

                                    setUpObserver()
                                }
                                else {
                                    prefs.getUserData()?.let { vmProfile.fetchProductsListings1(it.docId,"desc") }
                                    setUpObserver()
                                }
                            }
                            else {
                                prefs.getUserData()?.let { vmProfile.fetchProductsListings1(it.docId,"desc") }
                                setUpObserver()
                            }
                        }
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    orderOfPrice=filterProducts[0]
                }
            }
        }





        binding.txtSearchProducts.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase(Locale.ROOT)
                if (!productsList.isEmpty()) {
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
                        .replaceFragmentsInViewpager(Chat_Messages_Fragment(), 1)

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }



    private fun setUpProfileImage() {
        val user = prefs.getUserData()
        user?.let {
            binding.toolbar.subtitle = it.fullName
            nameHeader.text=user.fullName
            emailHeader.text=user.email
            Glide.loadImageWithListener(requireContext(), user.profileImageUri, imageview1) {
                progressofImage.gone()
            }
        }
    }

    private fun setUpObserver() {
        vmProfile.fetchProductsListings.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    productsList = it.data!!
                    if (!productsList.isNullOrEmpty()) {
                        val navigater=findNavController()
                        productAdapter=AdapterProductsListings(productsList,requireContext(),
                            navigater,viewLifecycleOwner,childFragmentManager)
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
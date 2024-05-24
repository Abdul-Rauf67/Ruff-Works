package com.example.agrilinkup

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
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
import com.example.agrilinkup.utils.ProgressDialogUtil.dismissProgressDialog
import com.example.agrilinkup.utils.ProgressDialogUtil.showProgressDialog
import com.example.agrilinkup.utils.gone
import com.example.agrilinkup.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
         val view= binding.root
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


        val toolbar=view.findViewById<Toolbar>(R.id.toolbar)
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

        //navImage=view.findViewById(R.id.navHeaderImageView)


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                        binding.recyclerView.adapter = AdapterProductsListings(productsList,requireContext(),navigater)
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
package com.example.agrilinkup.View.Fragments.UserAccount

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.agrilinkup.ModelUser
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.R
import com.example.agrilinkup.View.VmProfile
import com.example.agrilinkup.VmAuth
import com.example.agrilinkup.databinding.FragmentUpdateProfileBinding
import com.example.agrilinkup.ui.ProfileRepository
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.ProgressDialogUtil.dismissProgressDialog
import com.example.agrilinkup.utils.ProgressDialogUtil.showProgressDialog
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
 * Use the [updateProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class updateProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentUpdateProfileBinding? = null
    private val binding get() = _binding!!
    private val ccp by lazy { binding.countryCodePicker1 }

    //Pref
    @Inject
    lateinit var preferenceManager: PreferenceManager

    //Views
    private lateinit var etFullName: EditText
    private lateinit var etAccountTye: String
    private lateinit var etPhoneNum: EditText
    private lateinit var etAddress: EditText
    private lateinit var profileImgUri: String
    private lateinit var userEmail: String
    private lateinit var userPassword: String
    private lateinit var etState:EditText
    private lateinit var etDistract:EditText

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
        _binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)

        val prefs=PreferenceManager(requireContext())
        val auth= FirebaseAuth.getInstance()
        val db= FirebaseFirestore.getInstance()
        val storage= FirebaseStorage.getInstance().getReference()
        val context=requireContext()
        val mutableList= mutableListOf(auth,db,storage,prefs,context)
        //vm = ViewModelProvider(this).get(VmAuth::class.java)
       // vm = VmAuth(auth,db,storage,prefs,context)
        val profileRepository=ProfileRepository(db,auth,prefs,storage,context)
        vmProfile=VmProfile(profileRepository)

        preferenceManager=PreferenceManager(requireContext())

        inIt()
        var Seasons= arrayOf("Farmer","Consumer","Trader")
        if (binding.accountType!=null) {
            var adapter1= ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,Seasons)
            binding.accountType.adapter = adapter1
            1.also { binding.accountType.id = it }
            binding.accountType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long){
                    etAccountTye=Seasons[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    etAccountTye=Seasons[0]
                }
            }
        }
        return binding.root
    }

    private fun inIt() {
        inItViews()
        getUserFromPrefAndSet()
        setOnClickListener()
        setUpObserver()
    }

    private fun inItViews() {
        ccp.registerCarrierNumberEditText(binding.phoneNumber)
        etFullName = binding.nameReg
        etPhoneNum = binding.phoneNumber
        etState=binding.stateReg
        etDistract=binding.districtReg
        etAddress=binding.addressReg
    }

    private fun setOnClickListener() {

        binding.btnUpdate.setOnClickListener {
            if (validateForm()) {
                updateUser()
            }
        }
    }

    private fun updateUser() {
        val fullName = etFullName.text.toString().trim()
        val phoneNumber = etPhoneNum.text.toString().trim()
        val state=etState.text.toString().trim()
        val distract=etDistract.text.toString().trim()
        val address = etAddress.text.toString().trim()

        val user = ModelUser(
            userEmail,
            userPassword,
            fullName,
            state,
            distract,
            address,
            etAccountTye,
            phoneNumber,
            profileImgUri
        )
        vmProfile.updateUser(user)
    }

    private fun getUserFromPrefAndSet() {
        val user = preferenceManager.getUserData()
        user?.let {
            etFullName.setText(it.fullName)
            etPhoneNum.setText(it.phoneNumber)
            etAddress.setText(it.address)
            etState.setText(it.state)
            etDistract.setText(it.district)
            val eetAccountType=it.accountType
            this.profileImgUri = it.profileImageUri
            this.userEmail = it.email
            this.userPassword = it.password
        }
    }

    private fun setUpObserver() {
        vmProfile.updateStatus.observe(viewLifecycleOwner) {
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

        val name=binding.nameReg
        val phoneNumber=binding.phoneNumber
        val state=binding.stateReg
        val district=binding.districtReg
        val address=binding.addressReg

        name.error=null
        state.error=null
        district.error=null
        address.error=null

        return when{
            name.ifEmpty("Name is Required")->false
            state.ifEmpty("State Required")-> false
            district.ifEmpty("District is Required")->false
            address.ifEmpty("Address is required")->false
            phoneNumber.ifEmpty("Phone Number is required")->false

            !ccp.isValidFullNumber -> {
                toast("Invalid phone number for ${ccp.selectedCountryName}")
                false
            }
            else -> {true}
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment updateProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            updateProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }





    override fun onDestroyView() {
        super.onDestroyView()
        ccp.deregisterCarrierNumberEditText()
        _binding = null
    }
}
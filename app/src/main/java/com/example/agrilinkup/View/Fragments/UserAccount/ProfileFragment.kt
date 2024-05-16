package com.example.agrilinkup.View.Fragments.UserAccount

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.View.VmProfile
import com.example.agrilinkup.databinding.FragmentProfileBinding
import com.example.agrilinkup.ui.ProfileRepository
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.Glide
import com.example.agrilinkup.utils.ProgressDialogUtil.dismissProgressDialog
import com.example.agrilinkup.utils.ProgressDialogUtil.showProgressDialog
import com.example.agrilinkup.utils.gone
import com.example.agrilinkup.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private lateinit var vmProfile: VmProfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(com.example.agrilinkup.View.Fragments.UserAccount.ARG_PARAM1)
            param2 = it.getString(com.example.agrilinkup.View.Fragments.UserAccount.ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val prefs= PreferenceManager(requireContext())
        val auth= FirebaseAuth.getInstance()
        val db= FirebaseFirestore.getInstance()
        val storage= FirebaseStorage.getInstance().getReference()
        val context=requireContext()
        preferenceManager=prefs

        val profileRepo= ProfileRepository(db, auth, prefs, storage, context)
        vmProfile = VmProfile(profileRepo)
        inIt()
        return binding.root
    }

    private fun inIt() {
        setUserDataToViews()
        setOnClickListener()
        setUpObserver()
    }



    private fun setUserDataToViews() {
        val user = preferenceManager.getUserData()
        if (user != null) {
            binding.nameReg.text = user.fullName
            binding.emailReg.text = user.email
            binding.mobileReg.text=user.phoneNumber
            val completeAddress:String=user.address+",  " +user.district+",  " +user.state
            binding.addressReg.text=completeAddress
            binding.accountType.text=user.accountType

            Glide.loadImageWithListener(
                requireContext(),
                user.profileImageUri,
                binding.imgProfilePic
            ) {
                binding.pgProfileImage.gone()
            }
        }
    }


    private fun setOnClickListener() {

        binding.btnPickPhoto.setOnClickListener {
            openPhotoPicker()
        }
    }


    private fun setUpObserver() {
        vmProfile.userPicUpdate.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
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



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(com.example.agrilinkup.View.Fragments.UserAccount.ARG_PARAM1, param1)
                    putString(com.example.agrilinkup.View.Fragments.UserAccount.ARG_PARAM2, param2)
                }
            }
    }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.imgProfilePic.setImageURI(uri)
                vmProfile.updatePic(uri)
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }
    private fun openPhotoPicker() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
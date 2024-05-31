package com.example.agrilinkup.View.Fragments.UserAccount

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.agrilinkup.FirebaseModule_ProvideFirebaseAuthFactory
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.R
import com.example.agrilinkup.View.Activities.LoginActivity
import com.example.agrilinkup.databinding.FragmentUserAccountBinding
import com.example.agrilinkup.utils.BackPressedExtensions.goBackPressed
import com.example.agrilinkup.utils.Dialogs
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserAccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding:FragmentUserAccountBinding
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var preferenceManager: PreferenceManager

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
        binding = FragmentUserAccountBinding.inflate(inflater, container, false)
        auth= FirebaseModule_ProvideFirebaseAuthFactory.provideFirebaseAuth()
        preferenceManager= PreferenceManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.Profile.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment2_to_profileFragment3)
        }
        binding.myProducts.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment2_to_userProductsFragment2)
        }
        binding.addProduct.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment2_to_addProductListingFragment3)
        }
        binding.myOrders.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment2_to_userOrdersFragment2)
        }
        binding.updateProfile.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment2_to_updateProfileFragment)
        }






        binding.logoutAccount.setOnClickListener {
            logout()
        }
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
         * @return A new instance of fragment UserAccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserAccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun backPressed() = goBackPressed { requireActivity().finishAffinity() }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }


}
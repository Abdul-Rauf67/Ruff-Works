package com.example.agrilinkup.view.fragments.UserAccount

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.R
import com.example.agrilinkup.view.activities.LoginActivity
import com.example.agrilinkup.databinding.FragmentUserAccountBinding
import com.example.agrilinkup.utils.BackPressedExtensions.goBackPressed
import com.example.agrilinkup.utils.Dialogs
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserAccountFragment : Fragment() {

    private lateinit var binding:FragmentUserAccountBinding
    @Inject lateinit var auth: FirebaseAuth
    @Inject lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserAccountBinding.inflate(inflater, container, false)
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

    private fun backPressed() = goBackPressed { requireActivity().finishAffinity() }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }


}
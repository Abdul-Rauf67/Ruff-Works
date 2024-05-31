package com.example.flame.ui.fragments.messages.userlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agrilinkup.CartFragment
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.R
import com.example.agrilinkup.View.Adapters.AdapterAtCartRecyclerView
import com.example.agrilinkup.View.Fragments.MainFragment
import com.example.agrilinkup.View.VmProfile
import com.example.agrilinkup.databinding.FragmentCartBinding
//import com.example.agrilinkup.Utils.utils.VisibilityUtils.show
//import com.example.agrilinkup.Utils.utils.gone
import com.example.agrilinkup.databinding.FragmentMessagesUsersBinding
import com.example.agrilinkup.ui.ProfileRepository
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.ProgressDialogUtil.dismissProgressDialog
import com.example.agrilinkup.utils.ProgressDialogUtil.showProgressDialog
import com.example.agrilinkup.utils.toast
import com.example.flame.ui.fragments.messages.adapters.MessagesUserListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class MessagesUserListFragment : Fragment() {
    private lateinit var binding: FragmentMessagesUsersBinding
    private lateinit var vmProfile: VmProfile
    private lateinit var preferenceManager: PreferenceManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessagesUsersBinding.inflate(inflater, container, false)

        val prefs= PreferenceManager(requireContext())
        val auth= FirebaseAuth.getInstance()
        val db= FirebaseFirestore.getInstance()
        val storage= FirebaseStorage.getInstance().getReference()
        val context=requireContext()
        preferenceManager=prefs

        val profileRepo= ProfileRepository(db, auth, prefs, storage, context)
        vmProfile = VmProfile(profileRepo)
        vmProfile.fetchUserFormUserChatList()

        binding.refreshCartItems.setOnRefreshListener {
            binding.refreshCartItems.isRefreshing=false
            vmProfile.fetchUserFormUserChatList()
            setUpObserver1()
        }
        setUpObserver()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    fun refetchProduct(){
        vmProfile.fetchUserFormUserChatList()
        setUpObserver1()
    }




    private fun setUpObserver() {
        vmProfile.fetchUserFormUserChatList.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    val UsersList = it.data
                    //  toast("productList Empty")
                    if (!UsersList.isNullOrEmpty()) {
                        val navigator=findNavController()
                        binding.shimmerLayout.visibility=View.INVISIBLE
                        binding.refreshCartItems.visibility=View.VISIBLE
                        binding.recyclerView.adapter =
                            MessagesUserListAdapter(UsersList,requireContext(),navigator)
                    }
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

    private fun setUpObserver1() {
        vmProfile.fetchUserFormUserChatList.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    dismissProgressDialog()
                    val UsersList = it.data
                    //  toast("productList Empty")
                    if (!UsersList.isNullOrEmpty()) {
                        val navigator=findNavController()
                        binding.recyclerView.adapter =
                            MessagesUserListAdapter(UsersList,requireContext(),navigator)
                    }
                    //dismissProgressDialog()
                }

                is DataState.Error -> {
                    toast(it.errorMessage)
                    dismissProgressDialog()
                }

                is DataState.Loading -> {
                    // showProgressDialog()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }
}
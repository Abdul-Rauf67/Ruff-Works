package com.example.agrilinkup.view.messages.userlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.view.VmProfile
import com.example.agrilinkup.databinding.FragmentMessagesUsersBinding
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.ProgressDialogUtil.dismissProgressDialog
import com.example.agrilinkup.utils.toast
import com.example.flame.ui.fragments.messages.adapters.MessagesUserListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MessagesUserListFragment : Fragment() {
    private lateinit var binding: FragmentMessagesUsersBinding
    private val vmProfile: VmProfile by viewModels()
    @Inject lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessagesUsersBinding.inflate(inflater, container, false)

        vmProfile.fetchUserFormUserChatList()

        binding.refreshCartItems.setOnRefreshListener {
            binding.refreshCartItems.isRefreshing=false
            vmProfile.fetchUserFormUserChatList()
            setUpObserver1()
        }
        setUpObserver()

        return binding.root
    }

    fun reFetchProduct(){
        vmProfile.fetchUserFormUserChatList()
        setUpObserver1()
    }

    private fun setUpObserver() {
        vmProfile.fetchUserFormUserChatList.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    val usersList = it.data
                    //  toast("productList Empty")
                    if (!usersList.isNullOrEmpty()) {
                        val navigator=findNavController()
                        binding.shimmerLayout.visibility=View.INVISIBLE
                        binding.refreshCartItems.visibility=View.VISIBLE
                        binding.recyclerView.adapter =
                            MessagesUserListAdapter(usersList,requireContext(),navigator)
                    }
                    dismissProgressDialog()
                }

                is DataState.Error -> {
                    toast(it.errorMessage)
                    dismissProgressDialog()
                }

                is DataState.Loading -> {
                    //showProgressDialog()
                }
            }
        }
    }

    private fun setUpObserver1() {
        vmProfile.fetchUserFormUserChatList.observe(viewLifecycleOwner) {
            when (it) {
                is DataState.Success -> {
                    dismissProgressDialog()
                    val usersList = it.data
                    //  toast("productList Empty")
                    if (!usersList.isNullOrEmpty()) {
                        val navigator=findNavController()
                        binding.recyclerView.adapter =
                            MessagesUserListAdapter(usersList,requireContext(),navigator)
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
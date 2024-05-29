package com.example.flame.ui.fragments.messages.userlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.agrilinkup.R
//import com.example.agrilinkup.Utils.utils.VisibilityUtils.show
//import com.example.agrilinkup.Utils.utils.gone
import com.example.agrilinkup.databinding.FragmentMessagesUsersBinding
import com.example.flame.ui.fragments.messages.adapters.MessagesUserListAdapter


class MessagesUserListFragment : Fragment() {
    private lateinit var binding: FragmentMessagesUsersBinding
    private lateinit var vm : MessagesUserListVm
    private lateinit var messagesUserListAdapter: MessagesUserListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_messages_users, container, false)
        vm = ViewModelProvider(this)[MessagesUserListVm::class.java]
        inIt()
        return binding.root
    }

    private fun inIt() {
        setOnClickListener()
        populateData()
    }

    private fun populateData() {
        userObserver()
    }

    private fun userObserver(){
        binding.shimmerLayout.startShimmer()
        vm.usersList().observe(requireActivity()) { usersList ->
            messagesUserListAdapter = MessagesUserListAdapter(usersList){ userName,userId,profileImageLink->
                val bundle = Bundle()
                bundle.apply {
                    putString("userName",userName)
                    putString("receiverUid",userId)
                    putString("profileImageLink",profileImageLink)
                }
                findNavController().navigate(R.id.action_mainFragment2_to_messagesChatFragment,bundle)
            }
            binding.recyclerView.apply {
                adapter = messagesUserListAdapter
                binding.shimmerLayout.stopShimmer()
              //  binding.shimmerLayout.gone()
              //  this.show()
            }
        }
    }

    private fun setOnClickListener() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }
}
package com.example.agrilinkup.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.agrilinkup.R
import com.example.agrilinkup.databinding.FragmentChatMessagesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatMessagesFragment : Fragment() {
    private lateinit var binding: FragmentChatMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentChatMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu,inflater: MenuInflater){
        inflater.inflate(R.menu.chating,menu)
        super.onCreateOptionsMenu(menu,inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_settings ->{
                Toast.makeText(requireContext(),"Setting option selected",Toast.LENGTH_LONG).show()
            }
            R.id.clearchat ->{
                Toast.makeText(requireContext(),"clear chat option selected",Toast.LENGTH_LONG).show()
            }
            else ->{
                return super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
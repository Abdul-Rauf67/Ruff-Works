package com.example.agrilinkup

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.agrilinkup.view.Adapters.ViewPagerAdapterChats
import com.example.agrilinkup.databinding.FragmentChatBinding
import com.google.android.material.tabs.TabLayout


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding

    private var param1: String? = null
    private var param2: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val view = inflater.inflate(R.layout.fragment_chat, container, false)

        val viewPager: ViewPager = view.findViewById(R.id.viewpager)
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)

        // Initialize ViewPager adapter
        val adapter = ViewPagerAdapterChats(childFragmentManager)
        viewPager.adapter = adapter

        // Connect TabLayout with ViewPager
        tabLayout.setupWithViewPager(viewPager)




        return view
    }



    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
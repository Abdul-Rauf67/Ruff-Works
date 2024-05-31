package com.example.flame.ui.fragments.messages.video_image_viewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.agrilinkup.databinding.FragmentMessagesImageViewerBinding

class MessagesImageViewerFragment :Fragment() {
    private lateinit var binding: FragmentMessagesImageViewerBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMessagesImageViewerBinding.inflate(layoutInflater,null,false)
        inIt()
        return binding.root
    }

    private fun inIt(){
        setOnClickListeners()
        gettingAndSettingImageLink()
    }

    private fun gettingAndSettingImageLink() {
        val imageDownloadLink = arguments?.getString("imageUri")
        if (imageDownloadLink != null) {
            Glide.with(requireContext())
                .load(imageDownloadLink)
                .into(binding.imgViewer)
        }
    }

    private fun setOnClickListeners() {
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }
}
package com.example.agrilinkup.view.messages.video_image_viewer

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.agrilinkup.Utils.utils.CustomMediaController
import com.example.agrilinkup.databinding.FragmentMessagesVideoPlayBinding

class MessagesVideoPlayFragment :Fragment() {
    private lateinit var binding: FragmentMessagesVideoPlayBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMessagesVideoPlayBinding.inflate(layoutInflater,null,false)
        inIt()
        return binding.root
    }
    private fun inIt(){
        setOnClickListeners()
        gettingVideoUri()
    }

    private fun gettingVideoUri() {
        val uri = Uri.parse(arguments?.getString("videoUri"))
        if (uri != null) {
            binding.videoPlayer.apply {
                setVideoURI(uri)
                start()
                val mediaController = CustomMediaController(requireContext())
                mediaController.setAnchorView(this)
                setMediaController(mediaController)
            }
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


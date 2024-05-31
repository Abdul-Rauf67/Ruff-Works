package com.example.flame.ui.fragments.messages.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.agrilinkup.Models.Entities.messages.MessagesChatDataModel
import com.bumptech.glide.Glide
import com.example.agrilinkup.R
import com.example.agrilinkup.databinding.ItemChatAudioLeftBinding
import com.example.agrilinkup.databinding.ItemChatAudioRightBinding
import com.example.agrilinkup.databinding.ItemChatImgLeftBinding
import com.example.agrilinkup.databinding.ItemChatImgRightBinding
import com.example.agrilinkup.databinding.ItemChatLeftBinding
import com.example.agrilinkup.databinding.ItemChatRightBinding
import com.example.agrilinkup.databinding.ItemChatVideoLeftBinding
import com.example.agrilinkup.databinding.ItemChatVideoRightBinding
import com.example.agrilinkup.utils.setTimestamp

//import com.example.flame.R
//import com.example.flame.databinding.ItemChatAudioRightBinding
//import com.example.flame.databinding.ItemChatImgLeftBinding
//import com.example.flame.databinding.ItemChatImgRightBinding
//import com.example.flame.databinding.ItemChatLeftBinding
//import com.example.flame.databinding.ItemChatRightBinding
//import com.example.flame.databinding.ItemChatVideoLeftBinding
//import com.example.flame.databinding.ItemChatVideoRightBinding
//import com.example.flame.data.models.messages.MessagesChatDataModel
//import com.example.flame.databinding.ItemChatAudioLeftBinding
//import com.example.flame.utils.setTimestamp

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import me.jagar.chatvoiceplayerlibrary.VoicePlayerView

class MessagesChatAdapter(
    private val context: Context,
    private val chatList: ArrayList<MessagesChatDataModel>,
    private val sendVideoUriCallback:(String)->Unit,
    private val sendImgUriCallback:(String) ->Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val auth = Firebase.auth
    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 0
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 1
        private const val VIEW_TYPE_MESSAGE_IMAGE_SENT = 2
        private const val VIEW_TYPE_MESSAGE_IMAGE_RECEIVED = 3
        private const val VIEW_TYPE_MESSAGE_VIDEO_SENT = 4
        private const val VIEW_TYPE_MESSAGE_VIDEO_RECEIVED = 5
        private const val VIEW_TYPE_MESSAGE_AUDIO_SENT = 6
        private const val VIEW_TYPE_MESSAGE_AUDIO_RECEIVED = 7
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> {
                val binding = ItemChatRightBinding.inflate(inflater, parent, false)
                TextSendMessageViewHolder(binding)
            }

            VIEW_TYPE_MESSAGE_RECEIVED -> {
                val binding = ItemChatLeftBinding.inflate(inflater, parent, false)
                TextReceiveMessageViewHolder(binding)
            }

            VIEW_TYPE_MESSAGE_IMAGE_SENT -> {
                val binding = ItemChatImgRightBinding.inflate(inflater,parent,false)
                ImageSentMessageViewHolder(binding)
            }

            VIEW_TYPE_MESSAGE_IMAGE_RECEIVED ->{
                val binding = ItemChatImgLeftBinding.inflate(inflater,parent,false)
                ImageReceiveMessageViewHolder(binding)
            }

            VIEW_TYPE_MESSAGE_VIDEO_SENT ->{
                val binding = ItemChatVideoRightBinding.inflate(inflater,parent,false)
                VideoSentMessageViewHolder(binding)
            }

            VIEW_TYPE_MESSAGE_VIDEO_RECEIVED ->{
                val binding = ItemChatVideoLeftBinding.inflate(inflater,parent,false)
                VideoReceiveMessageViewHolder(binding)
            }

            VIEW_TYPE_MESSAGE_AUDIO_SENT ->{
                val binding = ItemChatAudioRightBinding.inflate(inflater,parent,false)
                AudioSentMessageViewHolder(binding)
            }

            VIEW_TYPE_MESSAGE_AUDIO_RECEIVED ->{
                val binding = ItemChatAudioLeftBinding.inflate(inflater,parent,false)
                AudioReceiveMessageViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = chatList[position]
        return when (item.messageType){
            "text" -> {
                if (item.senderUid == auth.currentUser?.uid) {
                    VIEW_TYPE_MESSAGE_SENT
                } else {
                    VIEW_TYPE_MESSAGE_RECEIVED
                }
            }
            "image" -> {
                if (item.senderUid == auth.currentUser?.uid) {
                    VIEW_TYPE_MESSAGE_IMAGE_SENT
                } else {
                    VIEW_TYPE_MESSAGE_IMAGE_RECEIVED
                }
            }
            "video" -> {
                if (item.senderUid == auth.currentUser?.uid) {
                    VIEW_TYPE_MESSAGE_VIDEO_SENT
                } else {
                    VIEW_TYPE_MESSAGE_VIDEO_RECEIVED
                }
            }
            "audio" -> {
                if (item.senderUid == auth.currentUser?.uid) {
                    VIEW_TYPE_MESSAGE_AUDIO_SENT
                } else {
                    VIEW_TYPE_MESSAGE_AUDIO_RECEIVED
                }
            }
            else -> throw IllegalArgumentException("Invalid message type")
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    abstract class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(message: MessagesChatDataModel)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = chatList[position]
        when (holder) {
            is MessageViewHolder -> {
                holder.bind(item)
            }
        }
    }

    inner class TextSendMessageViewHolder(val binding: ItemChatRightBinding) : MessageViewHolder(binding.root){
        private val sms = binding.chatRightSmsTv
        private val smsTime = binding.chatRightTimeTv
        override fun bind(message: MessagesChatDataModel) {
            sms.text = message.smsContent
            smsTime.setTimestamp(message.timestamp)
        }
    }

    inner class TextReceiveMessageViewHolder(val binding: ItemChatLeftBinding) : MessageViewHolder(binding.root) {
        private val sms = binding.chatLeftSmsTv
        private val smsTime = binding.chatLeftTimeTv
        override fun bind(message: MessagesChatDataModel) {
            sms.text = message.smsContent
            smsTime.setTimestamp(message.timestamp)
        }
    }

    inner class ImageSentMessageViewHolder(val binding: ItemChatImgRightBinding) : MessageViewHolder(binding.root) {
        private val img = binding.chatRightImg
        private val smsTime = binding.chatRightImgTimeTv
        private val progressBar = binding.imageProgressBar
        override fun bind(message: MessagesChatDataModel) {
            smsTime.setTimestamp(message.timestamp)
            if (message.smsContent != null) {

                progressBar.visibility = View.GONE
                Glide.with(itemView)
                    .load(message.smsContent)
                    .placeholder(R.drawable.loading)
                    .into(img)


                img.setOnClickListener {
                    sendImgUriCallback.invoke(message.smsContent!!)
                }
            } else if(message.demoSmsContent != null) {
                progressBar.visibility = View.VISIBLE
                val demoImgUri = message.demoSmsContent!!.toUri()
                img.setImageURI(demoImgUri)
            }
        }
    }

    inner class ImageReceiveMessageViewHolder(val binding: ItemChatImgLeftBinding) : MessageViewHolder(binding.root) {
        private val img = binding.chatLeftImg
        private val smsTime = binding.chatLeftImgTimeTv
        override fun bind(message: MessagesChatDataModel) {
            smsTime.setTimestamp(message.timestamp)
            if (message.smsContent != null){
                Glide.with(img)
                    .load(message.smsContent)
                    .placeholder(R.drawable.loading)
                    .into(img)
                img.setOnClickListener {
                    sendImgUriCallback.invoke(message.smsContent!!)
                }
            }
        }
    }

    inner class VideoSentMessageViewHolder(val binding: ItemChatVideoRightBinding) : MessageViewHolder(binding.root) {
        private val img = binding.chatRightVideoThumbnail
        private val smsTime = binding.chatRightVideoTimeTv
        override fun bind(message: MessagesChatDataModel) {
            smsTime.setTimestamp(message.timestamp)

            Glide.with(context)
                .load(message.videoThumbnail)
                .placeholder(R.drawable.loading)
                .into(img)

            img.setOnClickListener {
                val videoUri = message.smsContent
                if (videoUri != null) {
                    Log.d("video uri",videoUri)
                    sendVideoUriCallback.invoke(videoUri)
                }
            }
        }
    }

    inner class VideoReceiveMessageViewHolder(val binding: ItemChatVideoLeftBinding) : MessageViewHolder(binding.root) {
        private val img = binding.chatLeftVideoThumbnail
        private val smsTime = binding.chatLeftVideoTimeTv
        override fun bind(message: MessagesChatDataModel) {
            smsTime.setTimestamp(message.timestamp)

            Glide.with(context)
                .load(message.videoThumbnail)
                .placeholder(R.drawable.loading)
                .into(img)

            img.setOnClickListener {
                val videoUri = message.smsContent
                if (videoUri != null) {
                    sendVideoUriCallback.invoke(videoUri)
                }
            }
        }
    }

    inner class AudioSentMessageViewHolder(val binding: ItemChatAudioRightBinding) : MessageViewHolder(binding.root) {
        private val audioPlayer: VoicePlayerView = binding.voicePlayerView
        override fun bind(message: MessagesChatDataModel) {
            audioPlayer.setAudio(message.smsContent?.toUri().toString())
        }

    }
    inner class AudioReceiveMessageViewHolder(val binding: ItemChatAudioLeftBinding) : MessageViewHolder(binding.root) {
        private val audioPlayer: VoicePlayerView = binding.voicePlayerView
        override fun bind(message: MessagesChatDataModel) {
            audioPlayer.setAudio(message.smsContent?.toUri().toString())
        }

    }

}

package com.example.flame.ui.fragments.messages.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.agrilinkup.Models.Entities.messages.ChatUserListDataModel
import com.example.agrilinkup.R
import com.example.agrilinkup.utils.DateTimeUtils


class MessagesUserListAdapter(private val messagesList: ArrayList<ChatUserListDataModel>,
                              private val callback: (name:String,uid:String,profileImageLink:String) -> Unit) :
    RecyclerView.Adapter<MessagesUserListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = messagesList[position]
        holder.bin(itemPosition)
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName:TextView = itemView.findViewById(R.id.messageItemName)
        private val profileImage:ImageView = itemView.findViewById(R.id.messageProfileImg)
        private val lastSms:TextView = itemView.findViewById(R.id.messageItemSms)
        private val smsTime:TextView = itemView.findViewById(R.id.messageItemTime)
        fun bin(data: ChatUserListDataModel){
            userName.text = data.name
            smsTime.text = DateTimeUtils.formatDateTime(data.smsTime)

            Glide.with(itemView)
                .load(data.profileImgLink)
                .into(profileImage)

            itemView.setOnClickListener {
               // callback.invoke(data.name,data.uid,data.profileImgLink)
            }
        }
    }
}
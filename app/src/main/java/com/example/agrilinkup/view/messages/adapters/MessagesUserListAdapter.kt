package com.example.flame.ui.fragments.messages.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.agrilinkup.Models.Entities.ProductSharedPreferance
import com.example.agrilinkup.Models.Entities.messages.ChatUserListDataModel
import com.example.agrilinkup.R
import com.example.agrilinkup.utils.DateTimeUtils


class MessagesUserListAdapter(private val UsersList: List<ChatUserListDataModel>, val context: Context, val navigator: NavController) :
    RecyclerView.Adapter<MessagesUserListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.inbox_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return UsersList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val UserPosition = UsersList[position]
        holder.bind(UserPosition)
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName:TextView = itemView.findViewById(R.id.UserName)
        private val profileImage:ImageView = itemView.findViewById(R.id.image_circular)
       // private val lastSms:TextView = itemView.findViewById(R.id.LastMessageTxt)
        private val smsTime:TextView = itemView.findViewById(R.id.LastMessageTime)
        private val itemClickableLayout=itemView.findViewById<ConstraintLayout>(R.id.UserItem)
        fun bind(UserPosition: ChatUserListDataModel){
            userName.text = UserPosition.name
            smsTime.text = DateTimeUtils.formatDateTime(UserPosition.smsTime)

            Glide.with(itemView)
                .load(UserPosition.profileImgLink)
                .into(profileImage)

            profileImage.setOnClickListener{
                val builder= AlertDialog.Builder(context,R.style.CustomAlertDialog).create()
                val inflaterRaw=LayoutInflater.from(context)
                val view= inflaterRaw.inflate(R.layout.custom_product_image_view_layout,null)
                val text=view.findViewById<TextView>(R.id.productNameTxtView)
                text.text=UserPosition.name
                val  image=view.findViewById<ImageView>(R.id.productImageview)
                Glide.with(context)
                    .load(UserPosition.profileImgLink)
                    .into(image)
                val layoutRemove=view.findViewById<LinearLayout>(R.id.layour12)
                layoutRemove.visibility=View.INVISIBLE
//                val btnCart=view.findViewById<Button>(R.id.button2)
//                btnCart.text="Delete From Cart"
//                btnCart.setOnClickListener(View.OnClickListener {
//                    deleteProduct(product)
//                })
                builder.setView(view)
                builder.setCanceledOnTouchOutside(true)
                builder.show()
            }

            itemClickableLayout.setOnClickListener{
//                val preferenceManager= ProductSharedPreferance(context)
//                product.tem="CartFragment"
//                preferenceManager.saveTempProduct(product)
                val bundle = Bundle()
                bundle.apply {
                    putString("userName",UserPosition.name)
                    putString("receiverUid",UserPosition.UserUid)
                    putString("profileImageLink",UserPosition.profileImgLink)
                }
                navigator.navigate(R.id.action_mainFragment2_to_messagesChatFragment,bundle)
            }

        }
    }
}
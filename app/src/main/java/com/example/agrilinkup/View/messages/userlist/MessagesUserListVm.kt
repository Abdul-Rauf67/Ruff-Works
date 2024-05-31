package com.example.flame.ui.fragments.messages.userlist
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agrilinkup.Models.Entities.messages.ChatUserListDataModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MessagesUserListVm : ViewModel() {
    private val userListMutableLiveData = MutableLiveData<ArrayList<ChatUserListDataModel>>()
    private val database = Firebase.database.reference
    private val auth = Firebase.auth
    init {
        retrievingUsersList()
    }
    fun usersList(): LiveData<ArrayList<ChatUserListDataModel>>{
        return  userListMutableLiveData
    }

    private fun retrievingUsersList(){
        database.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = ArrayList<ChatUserListDataModel>()
                for (data in snapshot.children){
                    val currentUser = data.getValue(ChatUserListDataModel::class.java)
                    if (auth.currentUser?.uid != currentUser?.UserUid && currentUser != null){
                        userList.add(currentUser)
                    }
                    userListMutableLiveData.value = userList
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("getting users list",error.message)
            }
        })
    }
}
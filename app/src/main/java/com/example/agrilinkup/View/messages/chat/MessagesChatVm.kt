package com.example.flame.ui.fragments.messages.chat

import android.app.Application
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.agrilinkup.Models.Entities.messages.MessagesChatDataModel
import com.example.agrilinkup.utils.DateTimeUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream

class MessagesChatVm(application: Application) : AndroidViewModel(application) {
    private val chatListMutableLiveData = MutableLiveData<ArrayList<MessagesChatDataModel>>()
    val videoUploadProgress = MutableLiveData<Int>()
    val showProgressDialog = MutableLiveData<Boolean>()
    private val messageList = ArrayList<MessagesChatDataModel>()
    private val auth = Firebase.auth
    private val database = Firebase.database.reference
    private var storageRef = Firebase.storage.reference
    private var senderUid = auth.currentUser?.uid
    private var receiverUid: String? = null
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String


    val chatListLiveData: LiveData<ArrayList<MessagesChatDataModel>> = chatListMutableLiveData

    fun setReceiverUid(uid: String) {
        receiverUid = uid
        senderRoom = "$senderUid$receiverUid"
        receiverRoom = "$receiverUid$senderUid"
        retrieveMessages(senderRoom)
    }

    fun sendSms(sms: String) {
        val message = MessagesChatDataModel(senderUid, "text", sms)
        database.child("chats").child(senderRoom).child("messages").push().setValue(message)
            .addOnSuccessListener {
                database.child("chats").child(receiverRoom).child("messages").push()
                    .setValue(message)
            }
    }

    fun sendImage(imgUri: Uri) {
        val message = MessagesChatDataModel(senderUid, "image", null,null,imgUri.toString())
        messageList.add(message)
        chatListMutableLiveData.value = messageList

        val fileName = DateTimeUtils.simpleDateFormat()
        val imageRef = storageRef.child("chatImages/$fileName")
        imageRef.putFile(imgUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { imgLink ->
                    message.smsContent = imgLink.toString()
                    message.demoSmsContent = null
                    database.child("chats").child(senderRoom).child("messages").push().setValue(message)
                        .addOnSuccessListener {
                            database.child("chats").child(receiverRoom).child("messages").push().setValue(message)
                        }
                }
            }
    }

    fun sendVideo(videoUri: Uri) {
        val message = MessagesChatDataModel(senderUid,"video",null,null)

        val fileName = DateTimeUtils.simpleDateFormat()
        val imageRef = storageRef.child("videosThumbnailImages/$fileName")

        val thumbnail = getVideoThumbnailBytes(videoUri)
        if (thumbnail != null) {
            imageRef.putBytes(thumbnail)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { videoThumbnailUri ->
                        message.videoThumbnail = videoThumbnailUri.toString()
                    }
                }
        }

        val videoRef = storageRef.child("chatVideos/$fileName")
        videoRef.putFile(videoUri)
            .addOnProgressListener {taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                videoUploadProgress.value = progress
                showProgressDialog.value = true
            }

            .addOnSuccessListener {
                videoRef.downloadUrl.addOnSuccessListener { videoLink ->
                    message.smsContent = videoLink.toString()
                    database.child("chats").child(senderRoom).child("messages").push().setValue(message)
                        .addOnSuccessListener {
                            database.child("chats").child(receiverRoom).child("messages").push().setValue(message)
                        }
                    showProgressDialog.value = false
                }
            }
    }

    private fun getVideoThumbnailBytes(videoUri: Uri): ByteArray? {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        return try {
            mediaMetadataRetriever.setDataSource(getApplication(), videoUri)
            val thumbnailBitmap = mediaMetadataRetriever.getFrameAtTime(0)
            val byteArrayOutputStream = ByteArrayOutputStream()
            thumbnailBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            byteArrayOutputStream.toByteArray()
        } catch (e: Exception) {
            null
        } finally {
            mediaMetadataRetriever.release()
        }
    }

    private fun retrieveMessages(chatRoomId: String) {
        database.child("chats").child(chatRoomId).child("messages").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(MessagesChatDataModel::class.java)
                        message?.let { messageList.add(it) }
                    }
                    chatListMutableLiveData.value = messageList
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun sendAudio(audioUri: Uri) {
        val message = MessagesChatDataModel(senderUid,"audio",null,null,null)

        val fileName = DateTimeUtils.simpleDateFormat()
        val audioRef = storageRef.child("audios/$fileName")

        audioRef.putFile(audioUri)
            .addOnProgressListener {taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                videoUploadProgress.value = progress
                showProgressDialog.value = true
            }
            .addOnSuccessListener {
                audioRef.downloadUrl.addOnSuccessListener { audioUri ->
                    message.smsContent = audioUri.toString()
                    database.child("chats").child(senderRoom).child("messages").push().setValue(message)
                        .addOnSuccessListener {
                            database.child("chats").child(receiverRoom).child("messages").push().setValue(message)
                        }
                    showProgressDialog.value = false
                }
            }
    }


}
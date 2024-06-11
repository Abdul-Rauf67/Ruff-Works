@file:Suppress("DEPRECATION")

package com.example.flame.ui.fragments.messages.chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.devlomi.record_view.OnRecordListener
import com.example.agrilinkup.R
import com.example.agrilinkup.Utils.utils.handleSendButtonAndMicrophoneSwitchingOnTextChange
import com.example.agrilinkup.Utils.utils.invisible
import com.example.agrilinkup.Utils.utils.toast
import com.example.agrilinkup.Utils.utils.visible
import com.example.agrilinkup.databinding.FragmentMessagesChatBinding
import com.example.agrilinkup.utils.BottomSheets
import com.example.agrilinkup.utils.PermissionUtils
import com.example.agrilinkup.utils.extractSelectedUris
import com.example.agrilinkup.utils.filterUrisByMimeType
import com.example.agrilinkup.utils.gone
import com.example.agrilinkup.utils.pickMediaIntent

import com.example.flame.ui.fragments.messages.adapters.MessagesChatAdapter
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.File

@SuppressLint("ClickableViewAccessibility")
class MessagesChatFragment : Fragment() {
    private lateinit var binding: FragmentMessagesChatBinding
    private lateinit var vm: MessagesChatVm
    private lateinit var messagesAdapter: MessagesChatAdapter
    private lateinit var database:DatabaseReference
    private lateinit var auth:FirebaseAuth
    private  val permissionRequestCode = 100
    private  var mediaRecorder: MediaRecorder? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMessagesChatBinding.inflate(layoutInflater, null, false)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        initialize()
        return binding.root
    }

    private fun initialize() {
        initializeUIComponents()
        initializeViewModel()
        inItFirebase()
        setOnClickListeners()
        populateChatData()
        gettingAndSettingUserProfileData()
        handlingRecordButton()
    }

    private fun initializeViewModel() {
        vm = ViewModelProvider(this)[MessagesChatVm::class.java]
    }
    private fun inItFirebase(){
        database = Firebase.database.reference
        auth = Firebase.auth
    }
    private fun setOnClickListeners() {
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.linkDataIcon.setOnClickListener {
            BottomSheets.chatAttachBottomSheet(
                requireContext(),
                layoutInflater,
                camera = {openCamera()},
                gallery =  {openGallery()},
                audio = {audioPicker()}
            )

        }

        binding.messageEditText.requestFocus()

        binding.SendButton.setOnClickListener {
            if (binding.messageEditText.text?.isNotEmpty() == true){
                vm.sendSms(binding.messageEditText.text.toString())
                binding.messageEditText.text!!.clear()
            }
        }
    }


    private fun initializeUIComponents() {
        handleSendButtonAndMicrophoneSwitchingOnTextChange(binding.messageEditText, binding.SendButton, binding.recordButton)
    }
    private fun gettingAndSettingUserProfileData(){
        val bundle = arguments
        if (bundle != null){
            val userName = bundle.getString("userName")
            val profileImageLink = bundle.getString("profileImageLink")
            val receiverUid = bundle.getString("receiverUid")

            if (receiverUid != null) {
                vm.setReceiverUid(receiverUid)
            }


            binding.userProfileName.text = userName
            Glide.with(requireActivity())
                .load(profileImageLink)
                .into(binding.userProfileImg)
        }
    }


    //Getting and showing data from view model
    private fun populateChatData() {
        observeChat()
    }
    private fun observeChat(){
        binding.shimmerLayout.startShimmer()
        vm.chatListLiveData.observe(requireActivity()){ chat ->
            chat?.let { arrayList ->
                messagesAdapter = MessagesChatAdapter(
                    binding.root.context,arrayList,
                    sendVideoUriCallback = {videoUri -> sendVideoUriToVideoFragment(videoUri)},
                    sendImgUriCallback = {imgDownloadLink -> sendImgUriToImgViewerFragment(imgDownloadLink)}
                )
                binding.recyclerView.apply {
                    adapter = messagesAdapter
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.gone()
                    visible()
                    scrollToPosition(arrayList.size -1 )
                }
            }
        }

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setCancelable(false)
        vm.videoUploadProgress.observe(viewLifecycleOwner) { progress ->
            progressDialog.setMessage("Sending $progress%")
        }

        vm.showProgressDialog.observe(viewLifecycleOwner) { show ->
            if (show) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        }

    }


    //sending data to other fragments
    private fun sendVideoUriToVideoFragment(uri:String){
        val bundle = Bundle()
        bundle.putString("videoUri",uri)
        findNavController().navigate(R.id.action_messagesChatFragment_to_messagesVideoPlayFragment,bundle)
    }
    private fun sendImgUriToImgViewerFragment(imageLink:String){
        val bundle = Bundle()
        bundle.putString("imageUri",imageLink)
        findNavController().navigate(R.id.action_messagesChatFragment_to_messagesImageViewerFragment,bundle)
    }


    //Handling permissions
    private fun permissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.RECORD_AUDIO
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
    private fun areAllPermissionsGranted():Boolean{
        return PermissionUtils.handlePermissions(requireActivity(),permissions(),permissionRequestCode)
    }


    //Handling pictures taken by camera using image picker library
    private val startForCameraImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data
        when (resultCode) {
            Activity.RESULT_OK -> {
                val fileUri = data?.data
                if (fileUri != null) {
                    vm.sendImage(fileUri)
                } else {

                    toast("Selected image URI is null")
                }
            }
            ImagePicker.RESULT_ERROR -> {
                val error = ImagePicker.getError(data)
                toast("ImagePicker Error: $error")
            }
            else -> {
                toast("Image picking task canceled")
            }
        }
    }
    private fun openCamera() {
        if (areAllPermissionsGranted()){
            ImagePicker.with(requireActivity())
                .cameraOnly()
                .crop()
                .createIntent {
                    startForCameraImageResult.launch(it)
                }
        }
    }


    //Handling images and videos picked
    private val pickMediaLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val selectedMediaUris = extractSelectedUris(data)

                val imageUris = filterUrisByMimeType(selectedMediaUris, "image")
                val videoUris = filterUrisByMimeType(selectedMediaUris, "video")

                imageUris.forEach { vm.sendImage(it) }
                videoUris.forEach { vm.sendVideo(it) }

            }
        }
    }
    private fun openGallery(){
        if (areAllPermissionsGranted()) {
            pickMediaLauncher.launch(pickMediaIntent())
        }
    }


    //Handling Audio Picker
    private val audioPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val uri = data.data
                if (uri != null) {
                    vm.sendAudio(uri)
                }
            }
        }
    }
    private fun audioPicker(){
        if (areAllPermissionsGranted()){
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "audio/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            audioPickerLauncher.launch(intent)
        }
    }


    private fun areAllPermissionsGranted12(): Boolean {
        val requiredPermissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            permissionRequestCode
        )
    }



    private fun handlingRecordButton() {
        binding.recordButton.apply {
            setRecordView(binding.recordView)
            setScaleUpTo(1.2f)
        }
        val chatEditText = binding.messageEditText
        val attach = binding.linkDataIcon

        binding.recordView.apply {
            setSoundEnabled(false)
            setOnRecordListener(object : OnRecordListener {
                override fun onStart() {
                    chatEditText.visibility = View.GONE
                    attach.visibility = View.INVISIBLE
                    if (areAllPermissionsGranted12()) {
                        startRecording()
                        Log.d("Record Audio", "Recording started")
                    } else {
                        requestPermissions()
                    }
                }

                override fun onCancel() {
                    chatEditText.visibility = View.VISIBLE
                    attach.visibility = View.VISIBLE
                    stopRecording()
                }

                override fun onFinish(recordTime: Long, limitReached: Boolean) {
                    chatEditText.visibility = View.VISIBLE
                    attach.visibility = View.VISIBLE
                    stopAndSaveRecording()
                }

                override fun onLessThanSecond() {
                    chatEditText.visibility = View.VISIBLE
                    attach.visibility = View.VISIBLE
                    stopRecording()
                }

                override fun onLock() {
                    // Optional: Handle lock recording if needed
                }
            })
        }
    }

    private fun startRecording() {
        try {
            val audioFilePath = outputFilePathForAudioRecord()
            Log.d("Record Audio", "Output file path: $audioFilePath")

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFilePath)
                prepare()
                start()
            }
            Log.d("Record Audio", "Recording started")
        } catch (e: Exception) {
            Log.e("Record Audio", "Failed to start recording", e)
        }
    }

    private fun stopAndSaveRecording() {
        stopRecording()
        val audioFilePath = outputFilePathForAudioRecord()
        Log.d("Record Audio", "Audio file path: $audioFilePath")
        if (isAudioFileExists(audioFilePath)) {
            val audioUri = getUriForRecordedAudio(audioFilePath)
            vm.sendAudio(audioUri)
            // Send the audio URI to your ViewModel or use it as needed
            Log.e("Record Audio", "Audio URI: $audioUri")
        } else {
            Log.e("Record Audio", "Audio file does not exist")
        }
    }

    private fun stopRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            Log.d("Record Audio", "Recording stopped")
        } catch (e: Exception) {
            Log.e("Record Audio", "Failed to stop recording", e)
        }
    }

    private fun isAudioFileExists(filePath: String): Boolean {
        val audioFile = File(filePath)
        val exists = audioFile.exists() && audioFile.isFile
        Log.d("Record Audio", "File exists: $exists at path: $filePath")
        return exists
    }

    private fun outputFilePathForAudioRecord(): String {
        val directory = requireContext().filesDir
        if (!directory.exists()) {
            val created = directory.mkdirs()
            Log.d("Record Audio", "Directory created: $created")
        }
        return File(directory, "recorded_audio.mp3").absolutePath
    }

    private fun getUriForRecordedAudio(filePath: String): Uri {
        val audioFile = File(filePath)
        return Uri.fromFile(audioFile)
    }



    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        vm.chatListLiveData.removeObservers(viewLifecycleOwner)
    }
}
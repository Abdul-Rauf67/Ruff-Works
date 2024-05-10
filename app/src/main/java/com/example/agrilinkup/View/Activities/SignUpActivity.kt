package com.example.agrilinkup.View.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.agrilinkup.utils.toast
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.example.agrilinkup.R
import com.example.agrilinkup.databinding.ActivitySignUpBinding
import com.google.android.gms.cast.framework.media.ImagePicker

class SignUpActivity : AppCompatActivity() {

   // private val vm: VmAuth by viewModels()
    private lateinit var profileImgUri: Uri

    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)


        binding.registerBtn.setOnClickListener {

            startActivity(Intent(this, LoginActivity::class.java))
        }

        val context=this
        var Seasons= arrayOf("Farmer","Consumer","Trader")
        if (binding.accountType!=null) {
            var adapter1= ArrayAdapter(this,android.R.layout.simple_spinner_item,Seasons)
            binding.accountType.adapter = adapter1
            1.also { binding.accountType.id = it }
            binding.accountType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long){
                    Toast.makeText(context, "Profile clicked", Toast.LENGTH_SHORT).show()


                    Toast.makeText(context,"Spinner Position: ${position} and Season :${Seasons[position]}",Toast.LENGTH_LONG).show()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }
            }
        }
        binding.ProfilePhoto.setOnClickListener {
            setUpImagePicker()
        }
    }


    private var isImageChanged = false
    private val launcherForImagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val uri = data?.data
                    if (uri != null) {
                        profileImgUri = uri
                        binding.ProfilePhoto.setImageURI(data.data)
                        isImageChanged = true
                    }
                }

                com.github.dhaval2404.imagepicker.ImagePicker.RESULT_ERROR -> {
                    toast(com.github.dhaval2404.imagepicker.ImagePicker.getError(data))
                }

                else -> {
                    toast("Task Cancelled")
                }
            }
        }




private fun setUpImagePicker(){
    com.github.dhaval2404.imagepicker.ImagePicker.with(this)
        .crop()
        .compress(500)
        .createIntent {
            launcherForImagePicker.launch(it)
        }
}
}

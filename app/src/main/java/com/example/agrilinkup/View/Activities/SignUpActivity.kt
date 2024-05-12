package com.example.agrilinkup

import com.example.agrilinkup.VmAuth
import com.example.agrilinkup.ModelUser
import android.app.Activity
import com.example.agrilinkup.utils.ProgressDialogUtil
import android.content.Context
import android.content.Intent
import com.example.agrilinkup.utils.toast
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.agrilinkup.MainActivity
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.R
import com.example.agrilinkup.View.Activities.LoginActivity
import com.example.agrilinkup.databinding.ActivitySignUpBinding
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.ifEmpty
import com.google.android.gms.cast.framework.media.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

   // private val vm: VmAuth by viewModels()
    private lateinit var profileImgUri: Uri
    private val ccp by lazy { binding.countryCodePicker1 }
    private lateinit var vm: VmAuth
    private lateinit var binding: ActivitySignUpBinding
    lateinit var accountType:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        val prefs=PreferenceManager(this)
        val auth=FirebaseAuth.getInstance()
        val db=FirebaseFirestore.getInstance()
        val storage=FirebaseStorage.getInstance().getReference()
        val context=this
        val mutableList= mutableListOf(auth,db,storage,prefs,context)
        //vm = ViewModelProvider(this).get(VmAuth::class.java)
        vm = VmAuth(auth,db,storage,prefs,context)

        inIt1()

        var Seasons= arrayOf("Farmer","Consumer","Trader")
        if (binding.accountType!=null) {
            var adapter1= ArrayAdapter(this,android.R.layout.simple_spinner_item,Seasons)
            binding.accountType.adapter = adapter1
            1.also { binding.accountType.id = it }
            binding.accountType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long){
                    accountType=Seasons[position]
                   }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    accountType=Seasons[0]
                }
            }
        }
        binding.ProfilePhoto.setOnClickListener {
            setUpImagePicker()
        }
        binding.loginActivityTxt.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))

        }
    }


    private fun inIt1() {
        ccp.registerCarrierNumberEditText(binding.phoneNumber)
        setOnClickListener()
        setupObserver()
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
                        binding.ProfilePhoto2.setImageURI(data.data)
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

    fun setOnClickListener(){
        binding.registerBtn.setOnClickListener {
            if (validateForm())
                createUserAccount()
        }

    }


    private fun validateForm():Boolean{

        val name=binding.nameReg
        val email=binding.emailReg
        val phoneNumber=binding.phoneNumber
        //val accountType=binding.accountType
        val state=binding.stateReg
        val district=binding.districtReg
        val address=binding.addressReg
        val password1=binding.passwordReg1
        val password2=binding.confirmPassReg2

        val passwordc1=binding.passwordReg1.text.toString()
        val passwordc2=binding.confirmPassReg2.text.toString()

        name.error=null
        email.error=null
        state.error=null
        district.error=null
        address.error=null
        password1.error=null
        password2.error=null

        return when{
            name.ifEmpty("Name is Required")->false
            email.ifEmpty("Email is required")->false

            !Patterns.EMAIL_ADDRESS.matcher(binding.emailReg.text.toString()).matches() -> {
                binding.emailReg.error = "Invalid email pattern"
                binding.emailReg.requestFocus()
                false
            }


            state.ifEmpty("State Required")-> false
            district.ifEmpty("District is Required")->false
            address.ifEmpty("Address is required")->false
            password1.ifEmpty("Create your strong Password")-> false
            password2.ifEmpty("Confrim the password")->false
            phoneNumber.ifEmpty("Phone Number is required")->false

            !ccp.isValidFullNumber -> {
                toast("Invalid phone number for ${ccp.selectedCountryName}")
                false
            }

            !isImageChanged -> {
                toast("Please Select an Image!")
                false
            }
            !passwordc1.equals(passwordc2)-> {
                password2.error="Passwords missMatch"
                Toast.makeText(this, "Password is not match", Toast.LENGTH_SHORT).show()
                false
            }

            else -> {true}
        }
    }
    private fun isValidEmail(email: String): Boolean {
        // Simple email validation using regex
        val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,})+\$")
        return email.matches(emailRegex)
    }



    private fun createUserAccount() {
        val email = binding.emailReg.text.toString()
        val password = binding.confirmPassReg2.text.toString()
        val Name = binding.nameReg.text.toString()
        val state = binding.stateReg.text.toString()
        val district = binding.districtReg.text.toString()

        val phoneNumber = ccp.fullNumberWithPlus
        val address = binding.addressReg.text.toString()

        Toast.makeText(this, "$email  \n $password", Toast.LENGTH_SHORT).show()

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate password format (e.g., minimum length)
        if (!isValidPassword(password)) {
            Toast.makeText(this, "Invalid password format", Toast.LENGTH_SHORT).show()
            return
        }


        val user = ModelUser(
            email,
            password,
            Name,
            state,
            district,
            address,
            accountType,
            phoneNumber,
            profileImgUri.toString()
        )

       // vm.signUpWithEmailPass(user,this)

        lifecycleScope.launch(Dispatchers.IO) {
            vm.signUpWithEmailPass(user)
        }
    }
    private fun isValidPassword(password: String): Boolean {
        // Password validation criteria (e.g., minimum length)
        return password.length >= 6 // Example: Password must be at least 6 characters long
    }




    private fun setupObserver() {
        val lifecycleOwner:LifecycleOwner=this
        vm.signUpStatus.observe(lifecycleOwner, Observer {
                dataState ->
            when (dataState) {
                is DataState.Success -> {
                    toast("Account created successfully")
                    ProgressDialogUtil.dismissProgressDialog()
                    startActivity(Intent(this,MainActivity::class.java))
                    this.finish()
                }

                is DataState.Error -> {
                    toast(dataState.errorMessage)
                    ProgressDialogUtil.dismissProgressDialog()
                }

                is DataState.Loading -> {
                    ProgressDialogUtil.showProgressDialog(supportFragmentManager)
                }
            }

        })

    }


    override fun onDestroy() {
        super.onDestroy()
        ccp.deregisterCarrierNumberEditText()
        binding.unbind()
    }

}

package com.example.agrilinkup.View.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.agrilinkup.MainActivity
import com.example.agrilinkup.Models.PreferenceManager
import com.example.agrilinkup.R
import com.example.agrilinkup.SignUpActivity
import com.example.agrilinkup.VmAuth
import com.example.agrilinkup.databinding.ActivityLoginBinding
import com.example.agrilinkup.utils.DataState
import com.example.agrilinkup.utils.ProgressDialogUtil.dismissProgressDialog
import com.example.agrilinkup.utils.ProgressDialogUtil.showProgressDialog
import com.example.agrilinkup.utils.setPasswordVisibilityToggle
import com.example.agrilinkup.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    private lateinit var vm: VmAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=DataBindingUtil.setContentView(this,R.layout.activity_login)

        val prefs= PreferenceManager(this)
        val auth= FirebaseAuth.getInstance()
        val db= FirebaseFirestore.getInstance()
        val storage= FirebaseStorage.getInstance().getReference()
        val context=this
        val mutableList= mutableListOf(auth,db,storage,prefs,context)
        //vm = ViewModelProvider(this).get(VmAuth::class.java)
        vm = VmAuth(auth,db,storage,prefs,context)
        inIt()

        binding.register.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }


    private fun inIt() {
        setUpUI()
        setOnClickListeners()
        setUpObserver()
    }

    private fun setOnClickListeners() {
        binding.loginBtn.setOnClickListener {
            if (isValid()) {
                login()
            }
        }
    }

    private fun login() {
        val email = binding.EmailLogin.text.toString().trim()
        val password = binding.passLogin.text.toString().trim()
        lifecycleScope.launch {
            vm.loginWithEmailPass(email, password)
        }
    }

    private fun setUpObserver() {
        vm.loginStatus.observe(this) { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    toast("Loged In successfully")
                    dismissProgressDialog()
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }

                is DataState.Error -> {
                    toast(dataState.errorMessage)
                    dismissProgressDialog()
                }

                is DataState.Loading -> {
                    showProgressDialog(supportFragmentManager)
                }
            }
        }
    }

    private fun setUpUI() {
        setPasswordVisibilityToggle(binding.passLogin)
    }

    private fun isValid(): Boolean {
        val email = binding.EmailLogin
        val password = binding.passLogin

        email.error = null
        password.error = null
        return when {
            email.text.isNullOrEmpty() -> {
                email.error = "Please fill!"
                email.requestFocus()
                false
            }

            password.text.isNullOrEmpty() -> {
                password.error = "Please fill!"
                password.requestFocus()
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches() -> {
                email.error = "Invalid email pattern"
                email.requestFocus()
                false
            }

            else -> {
                true
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }
}


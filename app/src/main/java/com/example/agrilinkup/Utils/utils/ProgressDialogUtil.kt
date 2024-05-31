package com.example.agrilinkup.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

object ProgressDialogUtil {

    private var progressDialog: ProgressDialogFragment? = null

    fun Fragment.showProgressDialog() {
        progressDialog = ProgressDialogFragment()
        progressDialog?.show(this.parentFragmentManager, "progressDialog")
    }

    fun Fragment.dismissProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    fun dismissProgressDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    fun showProgressDialog(fragmentManager: FragmentManager) {
        progressDialog = ProgressDialogFragment()
        progressDialog?.show(fragmentManager, "progressDialog")
    }
}

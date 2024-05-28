package com.example.agrilinkup.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.example.agrilinkup.R
import com.example.agrilinkup.databinding.BottomSheetChatAttachBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

object BottomSheets {
//    fun logOutBottomSheets(
//        context: Context,
//        inflater: LayoutInflater,
//        logOut: () -> Unit,
//    ) {
//
//        val dialog = BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme)
//        val binding = LayoutBottomSheetLogoutBinding.inflate(inflater, null, false)
//
//        dialog.apply {
//            setContentView(binding.root)
//            setCancelable(true)
////            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            binding.btnYesLogout.setOnClickListener {
//                logOut.invoke()
//                dismiss()
//            }
//            binding.btnLogoutNo.setOnClickListener {
//                dismiss()
//            }
//        }
//        dialog.show()
//    }
//
//    fun bottomSheetChatMedia(
//        context: Context,
//        inflater: LayoutInflater,
//        camera:() -> Unit,
//        gallery:() -> Unit
//    ){
//        val dialog = BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme)
//        val binding = LayoutBottomsheetChatMediaBinding.inflate(inflater, null, false)
//
//        dialog.apply {
//            setContentView(binding.root)
//            setCancelable(true)
//            binding.camera.setOnClickListener {
//                camera.invoke()
//                dismiss()
//            }
//            binding.gallery.setOnClickListener {
//                gallery.invoke()
//                dismiss()
//            }
//        }
//        dialog.show()
//    }

    fun chatAttachBottomSheet(
        context: Context,
        inflater: LayoutInflater,
        camera: () -> Unit,
        gallery:() -> Unit,
        audio:() -> Unit
    ) {

        val dialog = BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme)
        val binding = BottomSheetChatAttachBinding.inflate(inflater, null, false)

        dialog.apply {
            setContentView(binding.root)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            binding.camera.setOnClickListener {
                camera.invoke()
                dismiss()
            }
            binding.gallery.setOnClickListener {
                gallery.invoke()
                dismiss()
            }
            binding.audio.setOnClickListener {
                audio.invoke()
                dismiss()
            }
        }

        binding.apply {

        }
        dialog.show()
    }

}


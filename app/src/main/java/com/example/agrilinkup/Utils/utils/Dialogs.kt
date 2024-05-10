package com.example.agrilinkup.utils

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import com.example.agrilinkup.databinding.DialogLogoutBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

object Dialogs {

    fun logoutDialog(
        context: Context,
        inflater: LayoutInflater,
        logout: () -> Unit
    ) {
        val dialog = Dialog(context)
        val binding = DialogLogoutBinding.inflate(inflater, null, false)
        dialog.apply {
            setContentView(binding.root)
            setCancelable(true)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            binding.btnYes.setOnClickListener {
                logout.invoke()
                dismiss()
            }
            binding.btnNo.setOnClickListener {
                dismiss()
            }
            show()
        }
    }

    inline fun permissionAlertDialog(context: Context,message:String,crossinline callback:() -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.apply{
            setMessage(message)
            setPositiveButton("Yes") { dialog, _ ->
                callback.invoke()
                dialog.dismiss()
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    inline fun showDatePickerDialog(
        context: Context,
        crossinline onDateSelected: (date: Date) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val date = selectedDate.time
                onDateSelected.invoke(date)
            }, year, month, day
        )
        datePickerDialog.show()
    }


}
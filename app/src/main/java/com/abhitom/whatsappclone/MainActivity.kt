package com.abhitom.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var phoneNumber:String
    private lateinit var countryCode:String
    private lateinit var alertDialogBox:MaterialAlertDialogBuilder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etPhoneNumber.addTextChangedListener {
            btnNext.isEnabled=!(it.isNullOrEmpty() || it.length<10)
        }
        btnNext.setOnClickListener {
            countryCode=countryCodeHolder.selectedCountryCodeWithPlus
            phoneNumber=countryCode+etPhoneNumber.text.toString()
            showDialogBox()
        }
    }

    private fun showDialogBox() {
        alertDialogBox=MaterialAlertDialogBuilder(this).apply {
            setMessage("We will be verifying the phone number:$phoneNumber\nIs this OK, or would you like to edit the number?")
            setPositiveButton("OK"){_, _ ->
                showLoginActivity()
            }
            setNegativeButton("Edit"){dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showLoginActivity() {
        startActivity(Intent(this, OTPActivity::class.java).putExtra("PhoneNumber", phoneNumber))
        finish()
    }
}
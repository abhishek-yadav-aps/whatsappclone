package com.abhitom.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_o_t_p.*

class OTPActivity : AppCompatActivity() {
    val PHONE_NUMBER="PhoneNumber"
    private lateinit var phoneNumber:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_t_p)
        phoneNumber=intent.getStringExtra(PHONE_NUMBER)
        tvVerifyNumber.text=getString(R.string.verify_number,phoneNumber)
        tvVerifyInfo.text="waiting for automatically detect an SMS sent to $phoneNumber"
    }
}
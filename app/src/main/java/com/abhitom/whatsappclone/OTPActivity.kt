package com.abhitom.whatsappclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import kotlinx.android.synthetic.main.activity_o_t_p.*
import java.util.*

class OTPActivity : AppCompatActivity() {
    val PHONE_NUMBER="PhoneNumber"
    private lateinit var phoneNumber:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_t_p)
        phoneNumber=intent.getStringExtra(PHONE_NUMBER)
        tvVerifyNumber.text=getString(R.string.verify_number,phoneNumber)
        setSpannableString()
    }

    private fun setSpannableString() {
        val span =SpannableString(getString(R.string.waiting_text,phoneNumber))
        val clickableSpan= object :ClickableSpan(){
            override fun onClick(widget: View) {

            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText=false
                ds.color=ds.linkColor
            }
        }
        span.setSpan(clickableSpan,span.length-13,span.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvVerifyInfo.text=span
        tvVerifyInfo.movementMethod=LinkMovementMethod.getInstance()
    }
}
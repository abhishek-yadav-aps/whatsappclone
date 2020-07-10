package com.abhitom.whatsappclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_o_t_p.*

class OTPActivity : AppCompatActivity() {
    val PHONE_NUMBER="PhoneNumber"
    private lateinit var phoneNumber:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_t_p)
        phoneNumber=intent.getStringExtra(PHONE_NUMBER)
        tvVerifyNumber.text=getString(R.string.verify_number,phoneNumber)
        setSpannableString()
        showCountDown(60000)
    }

    private fun showCountDown(startTime:Long) {
        btnResendCode.isEnabled=false
        object:CountDownTimer(startTime,1000){
            override fun onFinish() {
                btnResendCode.isEnabled=true
                tvCounter.isVisible=false
            }

            override fun onTick(millisUntilFinished: Long) {
                tvCounter.isVisible=true
                tvCounter.text=getString(R.string.second_remaining,millisUntilFinished/1000)
            }

        }.start()
    }

    private fun setSpannableString() {
        val span =SpannableString(getString(R.string.waiting_text,phoneNumber))
        val clickableSpan= object :ClickableSpan(){
            override fun onClick(widget: View) {
                startActivity(Intent(this@OTPActivity, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                finish()
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

    override fun onBackPressed() {
    }
}
package com.abhitom.whatsappclone

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_o_t_p.*
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity(), View.OnClickListener {
    val PHONE_NUMBER="PhoneNumber"
    private lateinit var phoneNumber:String
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var mResendToken:PhoneAuthProvider.ForceResendingToken?=null
    var mVerificationId:String?=null
    lateinit var progressDialog:ProgressDialog
    var mCountDownTimer: CountDownTimer?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_t_p)
        phoneNumber=intent.getStringExtra(PHONE_NUMBER)
        tvVerifyNumber.text=getString(R.string.verify_number,phoneNumber)
        btnResendCode.setOnClickListener(this)
        btnSendCode.setOnClickListener(this)
        setSpannableString()
        detectOTP()
        sentOTP()

    }

    private fun detectOTP() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }

                val smsCode=credential.smsCode
                if (!smsCode.isNullOrEmpty()){
                    etOTP.setText(smsCode)
                }
                signInWithPhoneAuthCredentials(credential)
            }
            override fun onVerificationFailed(e: FirebaseException) {
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }
                notifyUserAndRetry("Your Phone Number Verification is failed. Try again!")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                mVerificationId = verificationId
                mResendToken = token
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }
            }
        }
    }

    private fun signInWithPhoneAuthCredentials(credential: PhoneAuthCredential) {
        val mAuth=FirebaseAuth.getInstance()
        mAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }
            }
            else{
                notifyUserAndRetry("Your Phone Number Verification is failed. Try again!")
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }

            }
        }
    }

    private fun notifyUserAndRetry(message: String) {
        MaterialAlertDialogBuilder(this).apply {
            setMessage(message)
            setPositiveButton("Ok") { _, _ ->
                startActivity(Intent(this@OTPActivity, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                finish()
            }

            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            setCancelable(false)
            create()
            show()
        }
    }

    private fun sentOTP() {
        showCountDown(60000)
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks) // OnVerificationStateChangedCallbacks
        progressDialog=createProgressDialog("Sending a verification code",false)
        progressDialog.show()
    }

    private fun showCountDown(startTime:Long) {
        btnResendCode.isEnabled=false
        mCountDownTimer=object:CountDownTimer(startTime,1000){
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

    override fun onDestroy() {
        super.onDestroy()
        if (mCountDownTimer!=null){
            mCountDownTimer!!.cancel()
        }
    }

    override fun onClick(v: View?) {
        when(v){
            btnSendCode->{
                val code=etOTP.text.toString()
                if (!code.isNullOrEmpty() and !mVerificationId.isNullOrEmpty()){
                    progressDialog=createProgressDialog("Verifing..",false)
                    progressDialog.show()
                    val credential=PhoneAuthProvider.getCredential(mVerificationId!!,code)
                    signInWithPhoneAuthCredentials(credential)
                }
            }
            btnResendCode->{
                if (mResendToken!=null){
                    progressDialog=createProgressDialog("Sending Code Again..",false)
                    progressDialog.show()
                    showCountDown(60000)
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber, // Phone number to verify
                        60, // Timeout duration
                        TimeUnit.SECONDS, // Unit of timeout
                        this, // Activity (for callback binding)
                        callbacks,
                        mResendToken) // OnVerificationStateChangedCallbacks
                }
            }
        }
    }
}
fun Context.createProgressDialog(message: String, isCancelable: Boolean): ProgressDialog {
    return ProgressDialog(this).apply {
        setCancelable(isCancelable)
        setCanceledOnTouchOutside(false)
        setMessage(message)
    }
}
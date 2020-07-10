package com.abhitom.whatsappclone

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.auth.api.credentials.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var phoneNumber:String
    private lateinit var countryCode:String
    private lateinit var alertDialogBox:MaterialAlertDialogBuilder
    val PHONE_REQUEST=101
    var phoneNumberHint:String?=""
    val PHONE_NUMBER="PhoneNumber"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etPhoneNumber.addTextChangedListener {
            btnNext.isEnabled=!(it.isNullOrEmpty() || it.length<10)
        }
        val hintRequest = HintRequest.Builder()
            .setPhoneNumberIdentifierSupported(true)
            .setEmailAddressIdentifierSupported(false)
            .build()
        val options = CredentialsOptions.Builder()
            .forceEnableSaveDialog()
            .build()
        val pendingIntent = Credentials.getClient(this, options).getHintPickerIntent(hintRequest)
        startIntentSenderForResult(pendingIntent.intentSender, PHONE_REQUEST, null, 0, 0, 0)


        btnNext.setOnClickListener {
            countryCode=countryCodeHolder.selectedCountryCodeWithPlus
            phoneNumber=countryCode+etPhoneNumber.text.toString()
            showDialogBox()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PHONE_REQUEST) {
            if (data != null) {
                val credential: Credential? = data.getParcelableExtra(Credential.EXTRA_KEY)
                phoneNumberHint = credential?.id
                showPhoneNumberHint()
                // Some of our device fail to get phoneNumber here.
            }
        }
    }

    private fun showPhoneNumberHint() {
        val len=phoneNumberHint?.length
        phoneNumberHint=phoneNumberHint?.substring(3,len!!)
        etPhoneNumber.setText(phoneNumberHint)
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
        startActivity(Intent(this, OTPActivity::class.java).putExtra(PHONE_NUMBER, phoneNumber))
        finish()
    }
}
package com.abhitom.whatsappclone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        sivProfilePic.setOnClickListener {
            checkPermissionForImage()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermissionForImage() {
        if ((checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) &&
            (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)){
            val permissionRead= arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            val permissionWrite= arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permissionRead,1001)
            requestPermissions(permissionWrite,1002)
        }
        else{
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        val galleryIntent=Intent(Intent.ACTION_PICK)
        galleryIntent.type="image/*"
        startActivityForResult(galleryIntent,1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode== Activity.RESULT_OK && requestCode==1000){
            data?.data.let {
                sivProfilePic.setImageURI(it)
            }
        }
    }
}
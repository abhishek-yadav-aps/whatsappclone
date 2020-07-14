package com.abhitom.whatsappclone

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    val mStorage =FirebaseStorage.getInstance()
    val mAuth=FirebaseAuth.getInstance()
    var downloadUrl=""
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
            data?.data?.let {
                sivProfilePic.setImageURI(it)
                uploadImage(it)
            }
        }
    }

    private fun uploadImage(it: Uri) {
        btnNext.isEnabled=false
        val mRef=mStorage.reference.child("ProfilePhotoUploads/"+mAuth.uid.toString())
        val uploadTask=mRef.putFile(it)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>>{ task ->
            if (!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation mRef.downloadUrl
        }).addOnCompleteListener { task->
            btnNext.isEnabled=true
            if(task.isSuccessful){
                downloadUrl=task.result.toString()
                Log.i("URL","downloadUrl : $downloadUrl")
            }
        }
    }
}
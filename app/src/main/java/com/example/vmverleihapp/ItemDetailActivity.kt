package com.example.vmverleihapp

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_item_detail.*
import java.io.File

class ItemDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)
        val name = intent.getStringExtra(ITEM_DETAIL_NAME).toString()
        val description = intent.getStringExtra(ITEM_DETAIL_DESC).toString()
        val status = intent.getStringExtra(ITEM_DETAIL_STATUS).toString()
        val uri = intent.getStringExtra(ITEM_DETAIL_IMGURI).toString()
        Log.i("OnCliCk",name)
        Log.i("OnCliCk",description)
        Log.i("OnCliCk",status)
        Log.i("OnCliCk",uri)

        tvname.text = name
        tvDescription.text = description
        tvStatus.text = status
        val storageRef = FirebaseStorage.getInstance().reference.child("myImages/${uri}")
        val localFile = File.createTempFile("tempImage","jpg")
        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            tvimage.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Log.e("Adapter", "Fehler beim Laden des Bildes$storageRef")
        }
    }
    companion object {
        private const val ITEM_DETAIL_NAME = "ITEM_DETAIL_NAME"
        private const val ITEM_DETAIL_DESC = "ITEM_DETAIL_DESC"
        private const val ITEM_DETAIL_STATUS = "ITEM_DETAIL_STATUS"
        private const val ITEM_DETAIL_IMGURI = "ITEM_DETAIL_IMGURI"

    }
}
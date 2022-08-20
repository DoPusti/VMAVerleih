package com.example.vmverleihapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_items.*
import kotlinx.android.synthetic.main.activity_item_inquiry.*
import java.io.File

class ItemInquiryActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var name: String
    private lateinit var description: String
    private lateinit var status: String
    private lateinit var uri: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbarItemInquiry)
        setContentView(R.layout.activity_item_inquiry)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbarItemInquiry.setNavigationOnClickListener {
            onBackPressed()

        }

        name = intent.getStringExtra(ITEM_DETAIL_NAME).toString()
        description = intent.getStringExtra(ITEM_DETAIL_DESC).toString()
        status = intent.getStringExtra(ITEM_DETAIL_STATUS).toString()
        uri = intent.getStringExtra(ITEM_DETAIL_IMGURI).toString()

        firebaseAuth = FirebaseAuth.getInstance()
        tvName.text = name
        tvDescription.text =description
        val storageRef = FirebaseStorage.getInstance().reference.child("myImages/${uri}")
        val localFile = File.createTempFile("tempImage", "jpg")
        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            ivItemImage.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Log.e("Adapter", "Fehler beim Laden des Bildes$storageRef")
        }
        tvStatus.text = status
        buInquiry.setOnClickListener {
            //TODO Code von Daniel
        }
    }

    companion object {
        private const val ITEM_DETAIL_NAME = "ITEM_DETAIL_NAME"
        private const val ITEM_DETAIL_DESC = "ITEM_DETAIL_DESC"
        private const val ITEM_DETAIL_STATUS = "ITEM_DETAIL_STATUS"
        private const val ITEM_DETAIL_IMGURI = "ITEM_DETAIL_IMGURI"

    }
}
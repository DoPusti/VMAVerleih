package com.example.vmverleihapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_items.*
import kotlinx.android.synthetic.main.activity_item_inquiry.*
import kotlinx.android.synthetic.main.chat_user_row.view.*
import java.io.File

class ItemInquiryActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var name: String
    private lateinit var description: String
    private lateinit var status: String
    private lateinit var uri: String
    private lateinit var userid: String
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
        userid = intent.getStringExtra(ITEM_DETAIL_USERID).toString()

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

        if(FirebaseAuth.getInstance().uid == userid) {
            buInquiry.isEnabled = false
            buInquiry.isClickable = false
        }


        if(userid == firebaseAuth.uid){
            buInquiry.isVisible = false
        }
        else{
            buInquiry.setOnClickListener {

                val intent = Intent(this, ChatLogActivity::class.java)

                // TODO wenn vorhanden, latestMessage - read anpassen
                //val fromId = FirebaseAuth.getInstance().uid
                //val ref = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/").getReference("LatestMessages/$fromId/$toId")
                //ref.child("read").setValue(true)

                val ref = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Profile/$userid")
                ref.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(ChatUser::class.java)
                        if (user!= null) {
                            intent.putExtra(ChatsActivity.USER_KEY, user)
                            startActivity(intent)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
        }
    }

    companion object {
        private const val ITEM_DETAIL_NAME = "ITEM_DETAIL_NAME"
        private const val ITEM_DETAIL_DESC = "ITEM_DETAIL_DESC"
        private const val ITEM_DETAIL_STATUS = "ITEM_DETAIL_STATUS"
        private const val ITEM_DETAIL_IMGURI = "ITEM_DETAIL_IMGURI"
        private const val ITEM_DETAIL_USERID = "ITEM_DETAIL_USERID"

    }
}
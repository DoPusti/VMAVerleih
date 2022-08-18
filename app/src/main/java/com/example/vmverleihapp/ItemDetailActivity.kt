package com.example.vmverleihapp

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_item_detail.*
import java.io.File

class ItemDetailActivity : AppCompatActivity() {
    private lateinit var dbref: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var name : String
    private lateinit var description : String
    private lateinit var status: String
    private lateinit var uri : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)
        name = intent.getStringExtra(ITEM_DETAIL_NAME).toString()
        description = intent.getStringExtra(ITEM_DETAIL_DESC).toString()
        status = intent.getStringExtra(ITEM_DETAIL_STATUS).toString()
        uri = intent.getStringExtra(ITEM_DETAIL_IMGURI).toString()

        firebaseAuth = FirebaseAuth.getInstance()
        tvName.setText(name)
        tvDescription.setText(description)
        tvStatus.setText(status)
         val storageRef = FirebaseStorage.getInstance().reference.child("myImages/${uri}")
        val localFile = File.createTempFile("tempImage","jpg")
        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            ivImage.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Log.e("Adapter", "Fehler beim Laden des Bildes$storageRef")
        }
        buDelete.setOnClickListener {
            deleteItem()
        }
    }
    private fun deleteItem() {
        Log.i("Delete User", "DeleteUserData wird gestartet")
        dbref =
            FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userItems = userSnapshot.getValue(UserAuth::class.java)
                        if (userItems!!.mail == firebaseAuth.currentUser!!.email.toString()
                            && userItems.name == name && userItems.description == description
                            && userItems.status == status && userItems.imgUri == uri
                        ) {
                            val storageUri = userItems.imgUri
                            Log.i("Delete Item", "MailEintrag gefunden")
                            try {
                                userSnapshot.ref.removeValue()
                            } catch (e: NullPointerException) {
                                Log.e("Delete Item", "Nullpointerexcpetion $e")
                            }
                            deleteStorageUri(storageUri.toString())

                        }
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun deleteStorageUri(inImageUri : String) {
        // Create a storage reference from our app
        val storageReference = FirebaseStorage.getInstance().reference

        // Create a reference to the file to delete
        val desertRef = storageReference.child("myImages/$inImageUri")

        // Delete the file
        desertRef.delete().addOnSuccessListener {
            Toast.makeText(this,"Inserat wurde erfolgreich gelöscht",Toast.LENGTH_SHORT).show()
            Log.i("Delete User", "Erfolgreiches Löschen von $inImageUri")
            setResult(Activity.RESULT_OK)
            finish()
        }.addOnFailureListener {
            Log.i("Delete User", "Fehler beim Löschen von $inImageUri")
        }

    }
    companion object {
        private const val ITEM_DETAIL_NAME = "ITEM_DETAIL_NAME"
        private const val ITEM_DETAIL_DESC = "ITEM_DETAIL_DESC"
        private const val ITEM_DETAIL_STATUS = "ITEM_DETAIL_STATUS"
        private const val ITEM_DETAIL_IMGURI = "ITEM_DETAIL_IMGURI"

    }
}
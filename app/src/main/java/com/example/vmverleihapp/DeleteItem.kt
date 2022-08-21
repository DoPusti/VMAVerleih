package com.example.vmverleihapp

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlin.properties.Delegates

class DeleteItem(
    val inMail: String,
    val inName: String,
    val inDescription: String,
    val inStatus: String,
    val inImgUri: String
) {
    private lateinit var dbref: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    fun deleteItem(): Int {
        var itemRC = 0
        firebaseAuth = FirebaseAuth.getInstance()
        dbref =
            FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userItems = userSnapshot.getValue(UserAuth::class.java)
                        if (userItems!!.mail == firebaseAuth.currentUser!!.email.toString()
                            && userItems.name == inName && userItems.description == inDescription
                            && userItems.status == inStatus && userItems.imgUri == inImgUri
                        ) {
                            val storageUri = userItems.imgUri
                            Log.i("Delete Item", "MailEintrag gefunden")
                            try {
                                userSnapshot.ref.removeValue()
                                itemRC = 0
                            } catch (e: NullPointerException) {
                                itemRC = 100
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
        return itemRC
    }
    private fun deleteStorageUri(inImageUri: String) {
        // Create a storage reference from our app
        val storageReference = FirebaseStorage.getInstance().reference

        // Create a reference to the file to delete
        val desertRef = storageReference.child("myImages/$inImageUri")

        // Delete the file
        desertRef.delete().addOnSuccessListener {
            Log.i("Delete User", "Erfolgreiches Löschen von $inImageUri")
        }.addOnFailureListener {
            Log.i("Delete User", "Fehler beim Löschen von $inImageUri")
        }

    }



}
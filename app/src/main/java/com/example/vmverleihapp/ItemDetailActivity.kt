package com.example.vmverleihapp

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_item_detail.*
import java.io.File

class ItemDetailActivity : AppCompatActivity() {
    private lateinit var dbref: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var name: String
    private lateinit var description: String
    private lateinit var status: String
    private lateinit var uri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbarItemDetail)
        setContentView(R.layout.activity_item_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        name = intent.getStringExtra(ITEM_DETAIL_NAME).toString()
        description = intent.getStringExtra(ITEM_DETAIL_DESC).toString()
        status = intent.getStringExtra(ITEM_DETAIL_STATUS).toString()
        uri = intent.getStringExtra(ITEM_DETAIL_IMGURI).toString()

        toolbarItemDetail.setNavigationOnClickListener {
            onBackPressed()

        }

        firebaseAuth = FirebaseAuth.getInstance()
        tvName.setText(name)
        tvDescription.setText(description)
        //Status
        val stati = arrayOf<String>("Verfügbar", "Nicht verfügbar")
        // access the spinner
        if (spinnerStatus != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, stati
            )

            spinnerStatus.setAdapter(adapter)

            if (status == "Verfügbar") {
                spinnerStatus.setText(stati[0])
            } else {
                spinnerStatus.setText(stati[0])
            }

            spinnerStatus.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    Toast.makeText(
                        this@ItemDetailActivity,
                        stati[position], Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

        val storageRef = FirebaseStorage.getInstance().reference.child("myImages/${uri}")
        val localFile = File.createTempFile("tempImage", "jpg")
        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            ivImage.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Log.e("Adapter", "Fehler beim Laden des Bildes$storageRef")
        }
        buDelete.setOnClickListener {
            val deleteItemClass = DeleteItem(
                firebaseAuth.currentUser!!.email.toString(),
                name,
                description,
                status,
                uri
            )
            if(deleteItemClass.deleteItem() == 0) {
                Toast.makeText(this, "Inserat wurde erfolgreich gelöscht", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Fehler beim Löschen", Toast.LENGTH_SHORT).show()
            }
        }
        buUpdate.setOnClickListener {
            updateItem()
            setResult(Activity.RESULT_OK)
            Thread.sleep(500)
            finish()
        }
    }

    private fun updateItem() {
        Log.i("ItemUpdate", "Update wurde geklickt")
        dbref =
            FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        try {
                            val userItems = userSnapshot.getValue(UserAuth::class.java)

                            if (userItems!!.mail == firebaseAuth.currentUser!!.email.toString()
                                && userItems.name == name && userItems.description == description
                                && userItems.status == status && userItems.imgUri == uri
                            ) {
                                val userUpdate = UserAuth(
                                    name,
                                    description,
                                    userItems.mail,
                                    status,
                                    userItems.imgUri
                                )
                                userUpdate.name = tvName.text.toString()
                                userUpdate.description = tvDescription.text.toString()
                                userUpdate.status = spinnerStatus.text.toString()

                                Log.i("ItemUpdate", userUpdate.name.toString())
                                Log.i("ItemUpdate", userUpdate.description.toString())
                                Log.i("ItemUpdate", userUpdate.status.toString())

                                userSnapshot!!.key?.let {
                                    dbref.child(it).setValue(userUpdate).addOnCompleteListener {
                                        Toast.makeText(
                                            this@ItemDetailActivity,
                                            "Item erfolgreich geupdatet",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }.addOnFailureListener {

                                        Toast.makeText(
                                            this@ItemDetailActivity,
                                            "Fehler beim Update vom Item",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }


                            }
                        } catch (e: java.lang.NullPointerException) {
                            Log.e("UpdateUserData", "Keine Daten vorhanden")
                        }
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

   companion object {
        private const val ITEM_DETAIL_NAME = "ITEM_DETAIL_NAME"
        private const val ITEM_DETAIL_DESC = "ITEM_DETAIL_DESC"
        private const val ITEM_DETAIL_STATUS = "ITEM_DETAIL_STATUS"
        private const val ITEM_DETAIL_IMGURI = "ITEM_DETAIL_IMGURI"

    }
}
package com.example.vmverleihapp

import android.content.ClipData
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.vmverleihapp.RealtimeDatabases.DatabaseModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_items.*
import kotlinx.android.synthetic.main.activity_items.*

class AddItemsActivity : AppCompatActivity() {
    private lateinit var database : FirebaseDatabase
    private lateinit var referance  : DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbarAddItem)
        setContentView(R.layout.activity_add_items)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        firebaseAuth = FirebaseAuth.getInstance()
        toolbarAddItem.setNavigationOnClickListener {
            onBackPressed()

        }
        Log.i("AddItem", firebaseAuth.currentUser.toString())
        database = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
        referance = database.getReference("Users")
        buSave.setOnClickListener {
            sendData()
            val intent = Intent(this@AddItemsActivity,ItemsActivity::class.java)
            @Suppress("DEPRECATION")
            startActivity(intent)
        }

    }
    private fun sendData() {
        var name = etTitle.text.toString().trim()
        var description = etDescription.text.toString().trim()
        var user = firebaseAuth.currentUser.toString()
        if(name.isNotEmpty() && description.isNotEmpty()) {
            var model = DatabaseModel(name, description, user)
            var id = referance.push().key
            referance.child(id!!).setValue(model)
        }
        else {
            Toast.makeText(applicationContext,"All Fields Required",Toast.LENGTH_LONG).show()
        }
    }
}
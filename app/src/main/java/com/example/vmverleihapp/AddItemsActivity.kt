package com.example.vmverleihapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.vmverleihapp.RealtimeDatabases.DatabaseModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_items.*
import kotlinx.android.synthetic.main.activity_items.*

class AddItemsActivity : AppCompatActivity() {
    private lateinit var database : FirebaseDatabase
    private lateinit var referance  : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbarAddItem)
        setContentView(R.layout.activity_add_items)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbarAddItem.setNavigationOnClickListener {
            onBackPressed()

        }
        database = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
        referance = database.getReference("Users")
        buSave.setOnClickListener {
            sendData()
            val intent = Intent(this@AddItemsActivity,EditProfileActivity::class.java)
            @Suppress("DEPRECATION")
            startActivity(intent)
        }

    }
    private fun sendData() {
        var name = etTitle.text.toString().trim()
        var description = etDescription.text.toString().trim()
        if(name.isNotEmpty() && description.isNotEmpty()) {
            var model = DatabaseModel(name.toString(),description.toString())
            var id = referance.push().key
            referance.child(id!!).setValue(model)
        }
        else {
            Toast.makeText(applicationContext,"All Fields Required",Toast.LENGTH_LONG).show()
        }
    }
}
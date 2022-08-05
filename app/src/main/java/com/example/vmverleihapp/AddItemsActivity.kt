package com.example.vmverleihapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_items.*
import kotlinx.android.synthetic.main.activity_items.*

class AddItemsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbarAddItem)
        setContentView(R.layout.activity_add_items)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbarAddItem.setNavigationOnClickListener {
            onBackPressed()
        }
        buSave.setOnClickListener {
            val intent = Intent(this@AddItemsActivity,EditProfileActivity::class.java)
            @Suppress("DEPRECATION")
            startActivity(intent)
        }

    }
}
package com.example.vmverleihapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_items.*

class ItemsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
        setSupportActionBar(toolbarItems)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarItems.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}
package com.example.vmverleihapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.vmverleihapp.ItemsActivity.Companion.ITEM_ADD_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_items.*

class ItemsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbarItems)
        setContentView(R.layout.activity_items)
         supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarItems.setNavigationOnClickListener {
            onBackPressed()
        }
        fabAddItem.setOnClickListener {
            val intent = Intent(this@ItemsActivity,AddItemsActivity::class.java)
            @Suppress("DEPRECATION")
            startActivityForResult(intent, ITEM_ADD_REQUEST_CODE)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ITEM_ADD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
            } else {
                Log.e("Activity", "Abgebrochen oder zurück gedrückt")
            }
        }

    }
    companion object {
        private const val ITEM_ADD_REQUEST_CODE = 3

    }
}
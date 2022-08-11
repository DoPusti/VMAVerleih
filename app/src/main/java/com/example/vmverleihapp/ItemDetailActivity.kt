package com.example.vmverleihapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_item_detail.*

class ItemDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)
        val name = intent.getStringExtra(ITEM_DETAIL_NAME).toString()
        val description = intent.getStringExtra(ITEM_DETAIL_DESC).toString()
        val status = intent.getStringExtra(ITEM_DETAIL_STATUS).toString()
        val uri = intent.getStringExtra(ITEM_DETAIL_IMGURI).toString()

        tvname.text = name
        tvDescription.text = description
        tvStatus.text = status
    }
    companion object {
        private const val ITEM_DETAIL_NAME = "ITEM_DETAIL_NAME"
        private const val ITEM_DETAIL_DESC = "ITEM_DETAIL_DESC"
        private const val ITEM_DETAIL_STATUS = "ITEM_DETAIL_STATUS"
        private const val ITEM_DETAIL_IMGURI = "ITEM_DETAIL_IMGURI"

    }
}
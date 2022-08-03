package com.example.vmverleihapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var db : FirebaseDatabase = FirebaseDatabase.getInstance()
    private var root : DatabaseReference = db.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fabProfile.setOnClickListener{
            val intent = Intent(this, EditProfileActivity::class.java)
            @Suppress("DEPRECATION")
            startActivityForResult(intent,EDIT_PROFILE_ACTIVITY_REQUEST_CODE)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

            } else {
                Log.e("Activity", "Abgebrochen oder zurück gedrückt")
            }
        }

    }
    companion object {
        private const val EDIT_PROFILE_ACTIVITY_REQUEST_CODE = 1

    }
}
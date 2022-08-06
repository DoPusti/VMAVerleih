package com.example.vmverleihapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.vmverleihapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private lateinit var binding : ActivityMainBinding

    private var db : FirebaseDatabase = FirebaseDatabase.getInstance()
    private var root : DatabaseReference = db.reference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolBar)
        firebaseAuth = FirebaseAuth.getInstance()
        Log.i("UserMain", firebaseAuth.currentUser.toString())

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val intent = Intent(this, EditProfileActivity::class.java)
        @Suppress("DEPRECATION")
        startActivityForResult(intent,EDIT_PROFILE_ACTIVITY_REQUEST_CODE)

        return super.onOptionsItemSelected(item)
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
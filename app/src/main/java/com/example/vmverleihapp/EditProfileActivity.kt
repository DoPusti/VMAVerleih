package com.example.vmverleihapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_edit_profile.*


class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: EditProfileActivity
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbarProfile)

        setContentView(R.layout.activity_edit_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarProfile.setNavigationOnClickListener {
            onBackPressed()
        }
        firebaseAuth = FirebaseAuth.getInstance()
        et_email.setText(firebaseAuth.currentUser!!.email.toString())

        buViewItems.setOnClickListener {
            val intent = Intent(this@EditProfileActivity, ItemsActivity::class.java)
            @Suppress("DEPRECATION")
            startActivityForResult(intent, ITEM_VIEW_REQUEST_CODE)
        }
        buToMain.setOnClickListener {
            val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
            @Suppress("DEPRECATION")
            startActivity(intent)
        }
        if (et_email.text.isEmpty()) {
            et_email.hint = "Email"
        }
        if (et_first_name.text.isEmpty()) {
            et_first_name.hint = "Vorname"
        }
        if (et_last_name.text.isEmpty()) {
            et_last_name.hint = "Nachname"
        }
        if (et_contact_no.text.isEmpty()) {
            et_contact_no.hint = "Kontakt"
        }
        et_email.setOnFocusChangeListener { _, b ->
            if (!b) {
                et_email.hint = "Email"
            } else {
                et_email.hint = ""
            }

        }
        et_first_name.setOnFocusChangeListener { _, b ->
            if (!b) {
                et_first_name.hint = "Vorname"
            } else {
                et_first_name.hint = ""
            }

        }
        et_last_name.setOnFocusChangeListener { _, b ->
            if (!b) {
                et_last_name.hint = "Nachname"
            } else {
                et_last_name.hint = ""
            }

        }
        et_contact_no.setOnFocusChangeListener { _, b ->
            if (!b) {
                et_contact_no.hint = "Kontakt"
            } else {
                et_contact_no.hint = ""
            }

        }
        buLogOff.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this@EditProfileActivity, SignInActivity::class.java)
            @Suppress("DEPRECATION")
            startActivity(intent)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ITEM_VIEW_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
            } else {
                Log.e("Activity", "Abgebrochen oder zurück gedrückt")
            }
        }

    }

    companion object {
        private const val ITEM_VIEW_REQUEST_CODE = 2

    }
}
package com.example.vmverleihapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_profile.*


class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: EditProfileActivity
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbref: DatabaseReference

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
            setResult(Activity.RESULT_OK)
            finish()
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
            finish()

        }
        buUpdateProfil.setOnClickListener {
            updateProfil(
                et_first_name.text.toString(),
                et_last_name.text.toString(),
                et_contact_no.text.toString()
            )


        }
    }

    private fun updateProfil(inFirstName: String, inLastName: String, inContact: String) {

        dbref =
            FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Profil")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                /*
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        var profil = userSnapshot.getValue(Profil::class.java)
                        if (profil!!.email == firebaseAuth.currentUser!!.email.toString()) {

                            val empID = dbref.push().key!!
                            profil.contact = inContact
                            profil.nachname = inLastName
                            profil.vorname = inFirstName

                            dbref.child(empID).setValue(profil).addOnCompleteListener {
                                Toast.makeText(
                                    this@EditProfileActivity,
                                    "Profil erfolgreich geupdatet",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }.addOnFailureListener {

                                Toast.makeText(
                                    this@EditProfileActivity,
                                    "Fehler beim Update",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                */

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

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
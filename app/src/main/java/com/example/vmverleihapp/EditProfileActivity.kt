package com.example.vmverleihapp

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_edit_profile.*


class EditProfileActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbref: DatabaseReference
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbarProfile)

        setContentView(R.layout.activity_edit_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference


        toolbarProfile.setNavigationOnClickListener {
            onBackPressed()
        }
        firebaseAuth = FirebaseAuth.getInstance()
        et_email.setText(firebaseAuth.currentUser!!.email.toString())
        getProfilData()

        buViewItems.setOnClickListener {
            val intent = Intent(this@EditProfileActivity, ItemsActivity::class.java)
            @Suppress("DEPRECATION")
            startActivityForResult(intent, ITEM_VIEW_REQUEST_CODE)
        }
        editProfilImage.setOnClickListener {
            takePhotoWithCamera()

        }
        buDeleteUser.setOnClickListener {

            var dialog = AlertDialog.Builder(this)
            dialog.setTitle("Sind sie sicher?")
            dialog.setMessage("Beim Löschen dieses Account werden alle dazugehörigen Daten ebenfalls gelöscht!")


            dialog.setPositiveButton("Löschen") { _, _ ->
                Log.i("Delete User", "PositivButton gedrückt")
                deleteUserData()
                progressBar.visibility = View.VISIBLE
                val user = FirebaseAuth.getInstance().currentUser

                val credential =
                    EmailAuthProvider.getCredential("dominik.pustofka@gmx.net", "123456789")
                user!!.reauthenticate(credential)
                    .addOnCompleteListener { Log.i("Delete User", "Re-Authentifizierung") }


                user.delete().addOnCompleteListener { task ->


                    if (task.isSuccessful) {
                        progressBar.visibility = View.GONE
                        Log.i("EditProfile", "Account gelöscht")
                        Toast.makeText(this, "Daten wurden gelöscht!", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@EditProfileActivity, SignInActivity::class.java)
                        //deleteUserData()
                        FirebaseAuth.getInstance().signOut()
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }


                }


            }


            dialog.setNegativeButton("Abbrechen") { _, _ ->
                Toast.makeText(this, "Löschen wurde abgebrochen", Toast.LENGTH_LONG).show()

            }
            dialog.create()
            dialog.show()
        }
        editProfilImage.requestFocus()
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

    private fun deleteUserData() {
        Log.i("Delete User", "DeleteUserData wird gestartet")
        dbref =
            FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userItems = userSnapshot.getValue(UserAuth::class.java)
                        if (userItems!!.mail == firebaseAuth.currentUser!!.email.toString()) {
                            Log.i("Delete User", "MailEintrag gefunden")
                            try {
                                userSnapshot.ref.removeValue()
                            } catch (e: NullPointerException) {
                                Log.e("EditProfile", "Nullpointerexcpetion $e")
                            }

                        }
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        dbref = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
            .getReference("Profile")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val profil = userSnapshot.getValue(Profil::class.java)
                        if (profil!!.email  == firebaseAuth.currentUser!!.email.toString()) {
                            userSnapshot.ref.removeValue()
                        }
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    /* Auswahl 2 von "Bild hinzufügen" - Foto mit Kamera*/
    private fun takePhotoWithCamera() {

        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.CAMERA


            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()) {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        @Suppress("DEPRECATION")
                        startActivityForResult(cameraIntent, CAMERA)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRationaleDialogForPermissions()
                }

            }
            ).onSameThread()
            .check()
    }

    /* Wenn Berechtigung nicht vorliegt */
    private fun showRationaleDialogForPermissions() {
        AlertDialog.Builder(this).setMessage("Es liegen keine Berechtigungen vor")
            .setPositiveButton("Zu den Einstellungen") { _, _ ->
                try {
                    /* Einstellungen auf dem Gerät öffnen */
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    /* Paketname wird übergeben */
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()

                }


            }
            .setNegativeButton("Abbrechen") { dialog, _ ->
                dialog.dismiss()

            }.show()

    }


    private fun getProfilData() {
        dbref =
            FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Profile")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val profil = userSnapshot.getValue(Profil::class.java)
                        if (profil!!.email == firebaseAuth.currentUser!!.email.toString()) {
                            et_first_name.setText(profil.vorname.toString())
                            et_last_name.setText(profil.nachname.toString())
                            et_contact_no.setText(profil.contact.toString())
                        }
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun updateProfil(inFirstName: String, inLastName: String, inContact: String) {

        dbref =
            FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Profile")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val profil = userSnapshot.getValue(Profil::class.java)
                        val uuid = ""

                        if (profil!!.email == firebaseAuth.currentUser!!.email.toString()) {
                            /*
                            if (filePath != null) {
                                val progressDialog = ProgressDialog(this@EditProfileActivity)
                                progressDialog.setMessage("Inserat wird hochgeladen...")
                                progressDialog.setCancelable(false)
                                progressDialog.show()

                                uuid = UUID.randomUUID().toString()
                                val ref = storageReference?.child("myImages/$uuid")
                                ref?.putFile(filePath!!)?.addOnSuccessListener {
                                    Toast.makeText(
                                        this@EditProfileActivity,
                                        "Erfolgreich hochgeladen!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    if (progressDialog.isShowing) progressDialog.dismiss()
                                }
                                    ?.addOnFailureListener {
                                        if (progressDialog.isShowing) progressDialog.dismiss()
                                        Toast.makeText(
                                            this@EditProfileActivity,
                                            "Fehler beim Hochladen",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                            }

                             */



                            profil.contact = inContact
                            profil.nachname = inLastName
                            profil.vorname = inFirstName
                            profil.imgUri = uuid

                            userSnapshot!!.key?.let {
                                dbref.child(it).setValue(profil).addOnCompleteListener {
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
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ITEM_VIEW_REQUEST_CODE) {

            }
            if (requestCode == CAMERA) {
                val photoBitmap: Bitmap = data!!.extras!!.get("data") as Bitmap
                editProfilImage.setImageBitmap(photoBitmap)

            }
        } else {
            Log.e("Activity", "Abgebrochen oder zurück gedrückt")
        }
    }


    companion object {
        private const val ITEM_VIEW_REQUEST_CODE = 2
        private const val CAMERA = 3

    }
}
package com.example.vmverleihapp

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class EditProfileActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbref: DatabaseReference
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    private var filePath: Uri? = null

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
            val pictureDialog = AlertDialog.Builder(this)
            pictureDialog.setTitle("Bitte Aktion auswählen")
            val chooseOne: String = "Foto aus Galerie"
            val chooseTwo: String = "Foto aufnehmen"
            val pictureDialogItems = arrayOf(
                chooseOne,
                chooseTwo
            )
            pictureDialog.setItems(pictureDialogItems) { _, choose ->
                when (choose) {
                    0 -> launchGallery()
                    1 -> takePhotoWithCamera()
                }

            }
            pictureDialog.show()

        }
        buDeleteUser.setOnClickListener {

            Log.i("Delete User", "PositivButton gedrückt")
            val intent = Intent(this@EditProfileActivity, DeleteUserActivity::class.java)
            startActivityForResult(intent, DELETE_USER_REQUEST_CODE)
            progressBar.visibility = View.VISIBLE

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
            uploadImage()
            /*
            updateProfil(
                et_first_name.text.toString(),
                et_last_name.text.toString(),
                et_contact_no.text.toString()
            )


             */

        }

    }

    private fun uploadImage() {
        if (filePath != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Inserat wird hochgeladen...")
            progressDialog.setCancelable(false)
            progressDialog.show()

            val uuid = UUID.randomUUID().toString()
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
            updateProfil(
                et_first_name.text.toString(),
                et_last_name.text.toString(),
                et_contact_no.text.toString(),
                uuid
            )

        } else {
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE_REQUEST
        )
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
                            getImageUri(profil!!.imgUri.toString())
                        }
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


    }
    private fun getImageUri(inUri : String) {
        val storageRef =
        FirebaseStorage.getInstance().reference.child("myImages/${inUri}")
        Log.i("IMAGE", storageRef.toString())
        val localFile = File.createTempFile("tempImage", "jpg")
        storageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            editProfilImage.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Log.e("EditUser", "Fehler beim Laden des Bildes$storageRef")
        }
    }

    private fun updateProfil(
        inFirstName: String,
        inLastName: String,
        inContact: String,
        inUuid: String
    ) {

        dbref =
            FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Profile")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val profil = userSnapshot.getValue(Profil::class.java)

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
                            profil.imgUri = inUuid

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

    // Der Pfad wird zurückgegeben und eine Bitmap eingegeben
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        // Schnittstelle
        val wrapper = ContextWrapper(applicationContext)

        // Kann nur mit der App zugegriffen werden. Ist der Pfadname
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        // Name des Bildes (zufällige Zeichenfolge als png)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Stream um die Datei speichern zu können
            val stream = FileOutputStream(file)
            // Komprimieren als JPEG
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i("Add User", resultCode.toString())
        Log.i("Add User", requestCode.toString())
        Log.i("Add User", data.toString())
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ITEM_VIEW_REQUEST_CODE) {

            }
            if (requestCode == CAMERA) {
                val photoBitmap: Bitmap = data!!.extras!!.get("data") as Bitmap
                filePath = saveImageToInternalStorage(photoBitmap)
                editProfilImage.setImageBitmap(photoBitmap)

            }
            if (requestCode == DELETE_USER_REQUEST_CODE) {

            }
            if (requestCode == PICK_IMAGE_REQUEST) {
                if (data == null || data.data == null) {
                    return
                }

                filePath = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    Log.i("EditProfileActitivty", bitmap.toString())
                    editProfilImage.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else {
            Log.e("Activity", "Abgebrochen oder zurück gedrückt")
        }
    }


    companion object {
        private const val ITEM_VIEW_REQUEST_CODE = 2
        private const val CAMERA = 3
        private const val DELETE_USER_REQUEST_CODE = 4
        private const val PICK_IMAGE_REQUEST = 5
        private const val IMAGE_DIRECTORY = "ProfileImage"

    }
}
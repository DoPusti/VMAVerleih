package com.example.vmverleihapp


import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.vmverleihapp.RealtimeDatabases.DatabaseModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_items.*
import java.io.IOException
import java.util.*


class AddItemsActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var referance: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var imageURI: Uri
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    private var filePath: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbarAddItem)
        setContentView(R.layout.activity_add_items)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        firebaseAuth = FirebaseAuth.getInstance()
        toolbarAddItem.setNavigationOnClickListener {
            onBackPressed()

        }
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference


        database =
            FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
        referance = database.getReference("Users")
        buSave.setOnClickListener {
            uploadImage()
            Thread.sleep(1_000)
            setResult(Activity.RESULT_OK)
            finish()
        }
        buCancel.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }

        tvAddImage.setOnClickListener {
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


    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
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
                    this@AddItemsActivity,
                    "Erfolgreich hochgeladen!",
                    Toast.LENGTH_SHORT
                ).show()
                if (progressDialog.isShowing) progressDialog.dismiss()
            }
                ?.addOnFailureListener {
                    if (progressDialog.isShowing) progressDialog.dismiss()
                    Toast.makeText(
                        this@AddItemsActivity,
                        "Fehler beim Hochladen",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            sendData(uuid)

        } else {
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendData(ImageUUID: String) {
        val name = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val user = firebaseAuth.currentUser.toString()
        val email = firebaseAuth.currentUser!!.email.toString()
        val status = "Verfügbar"
        if (name.isNotEmpty() && description.isNotEmpty()) {
            val model = DatabaseModel(name, description, user, email, ImageUUID, status)
            val id = referance.push().key
            referance.child(id!!).setValue(model)
        } else {
            Toast.makeText(applicationContext, "All Fields Required", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                if (data == null || data.data == null) {
                    return
                }

                filePath = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    Log.i("ActivityResult",bitmap.toString())
                    ivPlaceImage.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
         } else if (requestCode == CAMERA) {
            Log.i("ActivityResult",data!!.extras!!.get("data").toString())
            val photoBitmap: Bitmap = data.extras!!.get("data") as Bitmap
            ivPlaceImage!!.setImageBitmap(photoBitmap)
         }
            else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("Cancelled", "Cancelled")
            }

        }

        companion object {
            // Codes für Intent onActivityResult
            private const val GALLERY = 1
            private const val CAMERA = 2
            private const val IMAGE = 3
            private const val PICK_IMAGE_REQUEST = 4


        }
    }
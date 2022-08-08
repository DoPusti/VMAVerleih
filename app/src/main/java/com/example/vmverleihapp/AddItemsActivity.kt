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
import kotlinx.android.synthetic.main.all_items.*
import kotlinx.android.synthetic.main.all_items.tvimage
import kotlinx.android.synthetic.main.user_item.*
import org.json.JSONException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AddItemsActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var referance: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var imageURI : Uri
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
            sendData()
            val intent = Intent(this@AddItemsActivity, ItemsActivity::class.java)
            @Suppress("DEPRECATION")
            startActivity(intent)
        }

        tvAddImage.setOnClickListener {
            /*
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
                    0 -> choosePhotoFromGallery()
                    1 -> takePhotoWithCamera()
                }

            }
            pictureDialog.show()

             */
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent,"Select Picture"), IMAGE)
        }

    }

    private fun sendData() {
        val name = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val user = firebaseAuth.currentUser.toString()
        val email = firebaseAuth.currentUser!!.email.toString()
        val imageUri = ""
        val status = "Verfügbar"
        if (name.isNotEmpty() && description.isNotEmpty()) {
            val model = DatabaseModel(name, description, user, email, imageUri, status)
            val id = referance.push().key
            referance.child(id!!).setValue(model)
        } else {
            Toast.makeText(applicationContext, "All Fields Required", Toast.LENGTH_LONG).show()
        }
    }
    /* Auswahl 1 von "Bild hinzufügen" - Foto aus Gallery */
    private fun choosePhotoFromGallery() {
        Dexter.withContext(this)
            .withPermissions(
                /* Prüfung ob wir die Berechtigung haben */
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            /* Listener wartet bis es akzeptiert wird, oder bereits vorhanden ist */
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()) {
                        val galleryIntent = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )

                        startActivityForResult(galleryIntent, GALLERY)


                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRationaleDialogForPermissions()
                }


            })
            /* Notwendig für die Ausführung */
            .onSameThread()
            .check()
    }
    /* Auswahl 2 von "Bild hinzufügen" - Foto mit Kamera*/
    private fun takePhotoWithCamera() {

        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        @Suppress("DEPRECATION")
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                        /* Das Bild soll nun auch im ImageView angezeigt werden */
                        ivPlaceImage!!.setImageBitmap(selectedImageBitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@AddItemsActivity,
                            "Fehlgeschlagen",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                }
            } else if (requestCode == CAMERA) {

                val photoBitmap: Bitmap = data!!.extras!!.get("data") as Bitmap
                /* Beispielausgabe :

                Saved Image:: Path: /data/user/0/com.example.vmagardener/app_PlantsImages/b963386c-a5e9-405f-9a85-4412252d447e.jpg

                 */


                ivPlaceImage!!.setImageBitmap(photoBitmap)

            } else if (requestCode == IMAGE) {
                filePath = data!!.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filePath)
                    tvimage.setImageBitmap(bitmap)

                }catch (e:IOException) {
                    e.printStackTrace()
                }
                //imageURI = data?.data!!
                //tvimage.setImageURI(imageURI)
                //uploadImage()

            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun uploadImage() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading File...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageReference.putFile(imageURI).
                addOnSuccessListener {
                    tvimage.setImageURI(null)
                    Toast.makeText(this@AddItemsActivity,"Succesfulty uploaded",Toast.LENGTH_SHORT).show()
                    if(progressDialog.isShowing) progressDialog.dismiss()
                }.addOnFailureListener{
                    if(progressDialog.isShowing) progressDialog.dismiss()
                    Toast.makeText(this@AddItemsActivity,"Upload Failed",Toast.LENGTH_SHORT).show()
        }
    }
    companion object {
        // Codes für Intent onActivityResult
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE = 3



    }
}
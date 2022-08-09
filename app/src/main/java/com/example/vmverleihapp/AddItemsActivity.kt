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
            uploadImage()
            val intent = Intent(this@AddItemsActivity, ItemsActivity::class.java)
            @Suppress("DEPRECATION")
            startActivity(intent)
        }

        tvAddImage.setOnClickListener {
            launchGallery()
        }

    }
    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }
    private fun uploadImage(){
        if(filePath != null){
            val ref = storageReference?.child("myImages/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)

        }else{
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                if(data == null || data.data == null){
                    return
                }

                filePath = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    ivPlaceImage.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "Cancelled")
        }
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
    }
    companion object {
        // Codes für Intent onActivityResult
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE = 3
        private const val PICK_IMAGE_REQUEST = 4



    }
}
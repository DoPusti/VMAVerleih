package com.example.vmverleihapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_delete_user.*
import okhttp3.Dispatcher
import kotlin.coroutines.CoroutineContext

class DeleteUserActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbref: DatabaseReference
    private var storageReference: StorageReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_user)
        val user = FirebaseAuth.getInstance().currentUser
        firebaseAuth = FirebaseAuth.getInstance()

        buDelete.setOnClickListener {

            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Sind sie sicher?")
            dialog.setMessage("Beim Löschen dieses Account werden alle dazugehörigen Daten ebenfalls gelöscht!")


            dialog.setPositiveButton("Löschen") { _, _ ->
                if (etEmail.text.toString().isNotEmpty() && etPass.text.toString().isNotEmpty()) {
                    deleteUserData(firebaseAuth.currentUser!!.email.toString())
                    val credential =
                        EmailAuthProvider.getCredential(
                            etEmail.text.toString(),
                            etPass.text.toString()
                        )
                    user!!.reauthenticate(credential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                user.delete().addOnCompleteListener { task ->

                                    if (task.isSuccessful) {
                                        //progressBar.visibility = View.GONE
                                        Log.i("EditProfile", "Account gelöscht")
                                        Toast.makeText(
                                            this,
                                            "Account wurde erfolgreich gelöscht!",
                                            Toast.LENGTH_LONG
                                        )
                                            .show()
                                        val intent =
                                            Intent(
                                                this@DeleteUserActivity,
                                                SignInActivity::class.java
                                            )

                                        FirebaseAuth.getInstance().signOut()
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        startActivity(intent)
                                    } else {
                                        Log.i("EditProfile", "Fehler beim Löschen des Accounts")
                                        Toast.makeText(
                                            this,
                                            "Löschen des Kontos fehlgeschlagen",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Authentifizierung fehlgeschlagen",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }

                        }


                } else {
                    Toast.makeText(
                        this,
                        "Bitte die notwendigen Felder ausfüllen",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }


            dialog.setNegativeButton("Abbrechen") { _, _ ->
                Toast.makeText(this, "Löschen wurde abgebrochen", Toast.LENGTH_LONG).show()

            }
            dialog.create()
            dialog.show()


        }
        buCancel.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
            setResult(Activity.RESULT_OK)
            finish()
        }

    }

    private fun deleteUserData(inMail: String) {
        Log.i("Delete User", "DeleteUserData wird gestartet")
        Log.i("Delete User", "inMail $inMail")
        dbref =
            FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userItems = userSnapshot.getValue(UserAuth::class.java)
                        if (userItems!!.mail == inMail) {
                            val storageUri = userItems.imgUri
                            Log.i("Delete User", "MailEintrag gefunden")
                            try {
                                userSnapshot.ref.removeValue()
                            } catch (e: NullPointerException) {
                                Log.e("EditProfile", "Nullpointerexcpetion $e")
                            }
                            deleteStorageUri(storageUri.toString())

                        }
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        val userId = FirebaseAuth.getInstance().uid

        dbref = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Messages/$userId")
        dbref.removeValue()

        dbref = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/").getReference("LatestMessages/$userId")
        dbref.removeValue()

        dbref = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Profile/$userId")
        dbref.removeValue()

    }
    private fun deleteStorageUri(inImageUri : String) {
        // Create a storage reference from our app
        val storageReference = FirebaseStorage.getInstance().reference

        // Create a reference to the file to delete
        val desertRef = storageReference.child("myImages/$inImageUri")

        // Delete the file
        desertRef.delete().addOnSuccessListener {
            Log.i("Delete User", "Erfolgreiches Löschen von $inImageUri")
        }.addOnFailureListener {
            Log.i("Delete User", "Fehler beim Löschen von $inImageUri")
        }

    }
}
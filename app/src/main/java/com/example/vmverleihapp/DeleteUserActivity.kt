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
import kotlinx.android.synthetic.main.activity_delete_user.*

class DeleteUserActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbref: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_user)
        val user = FirebaseAuth.getInstance().currentUser
        firebaseAuth = FirebaseAuth.getInstance()

        buDelete.setOnClickListener {

            var dialog = AlertDialog.Builder(this)
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
                                        )
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

        dbref =
            FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Profile")
        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val profil = userSnapshot.getValue(Profil::class.java)
                        if (profil!!.email == inMail) {
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
}
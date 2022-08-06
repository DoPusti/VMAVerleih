package com.example.vmverleihapp.RealtimeDatabases

import com.example.vmverleihapp.MyAdapter
import com.example.vmverleihapp.User
import com.example.vmverleihapp.UserAuth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FireBaseHandler {
    /*
    private lateinit var firebaseAuth: FirebaseAuth
    var dbref :DatabaseReference =  FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
    .getReference("Users")

    private fun getUserData() : User {
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userID = userSnapshot.getValue(UserAuth::class.java)
                        if (userID!!.user == firebaseAuth.currentUser.toString()) {
                            user = User(userID!!.name,userID!!.description)
                        }
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
        return user
    }

     */



}
package com.example.vmverleihapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_items.*

class ItemsActivity : AppCompatActivity() {
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)

        userRecyclerView = findViewById(R.id.userList)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf<User>()
        firebaseAuth = FirebaseAuth.getInstance()

        getUserData()
        fabAddItem.setOnClickListener {
            val intent = Intent(this@ItemsActivity, AddItemsActivity::class.java)
            @Suppress("DEPRECATION")
            startActivityForResult(intent, ITEM_ADD_REQUEST_CODE)
        }

    }
    private fun getUserData() {

        dbref =
            FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userID = userSnapshot.getValue(UserAuth::class.java)
                        if (userID!!.mail == firebaseAuth.currentUser!!.email.toString()) {
                            val user = User(userID!!.name,userID!!.description, userID!!.status)
                            if (user != null) {
                                userArrayList.add(user)
                            }
                        }
                    }
                    userRecyclerView.adapter = MyAdapter(userArrayList)
                }

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ITEM_ADD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
            } else {
                Log.e("Activity", "Abgebrochen oder zurück gedrückt")
            }
        }

    }

    companion object {
        private const val ITEM_ADD_REQUEST_CODE = 3

    }
}
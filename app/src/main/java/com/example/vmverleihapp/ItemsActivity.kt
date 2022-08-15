package com.example.vmverleihapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_items.*

class ItemsActivity : AppCompatActivity() {
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
        setSupportActionBar(toolbarUserItems)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbarUserItems.setNavigationOnClickListener {
            onBackPressed()
        }

        firebaseAuth = FirebaseAuth.getInstance()

        getUserData()
        fabAddItem.setOnClickListener {
            val intent = Intent(this@ItemsActivity, AddItemsActivity::class.java)
            @Suppress("DEPRECATION")
            startActivityForResult(intent, ITEM_ADD_REQUEST_CODE)
        }

    }
    private fun getUserData() {

        userArrayList = ArrayList<User>()
        dbref =
            FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userID = userSnapshot.getValue(UserAuth::class.java)
                        if (userID!!.mail == firebaseAuth.currentUser!!.email.toString()) {
                            val user = User(userID.name,userID!!.description, userID!!.status,userID!!.imgUri)
                            userArrayList.add(user)
                        }
                    }

                }
                if(userArrayList.size > 0) {
                    userList.visibility = View.VISIBLE
                    tvNoRecordsAvailable.visibility = View.GONE
                    setupItemRecyclerView(userArrayList)
                } else {
                    userList.visibility = View.GONE
                    tvNoRecordsAvailable.visibility = View.VISIBLE
                }

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun setupItemRecyclerView(itemList: ArrayList<User>) {
        userRecyclerView = findViewById(R.id.userList)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf()
        userRecyclerView.adapter = MyAdapter(this,itemList)


        val plantAdaper = MyAdapter( this,userArrayList)
        /*
        rvPlantList.adapter = plantAdaper

        plantAdaper.setOnClickListener(object : PlantAdapter.OnClickListener {
            override fun onClick(position: Int, model: PlantModel) {
                val intent = Intent(this@MainActivity, PlantDetailActivity::class.java)
                intent.putExtra(PLANT_OBJECT_DETAILS,model)
                startActivity(intent)
            }
        })
        */

        /*
        val editSwipeHandler = object  : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvPlantList.adapter as PlantAdapter
                adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition,
                    ADD_PLANT_ACTIVITY_REQUEST_CODE)
            }

        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rvPlantList)


        val deleteSwipeHandler = object  : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rvPlantList.adapter as PlantAdapter
                adapter.removeAt(viewHolder.adapterPosition)
                getPlantListFromLocalDB()
            }

        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rvPlantList)

         */
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ITEM_ADD_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getUserData()
            } else {
                Log.e("Activity", "Abgebrochen oder zurück gedrückt")
            }
        }

    }

    companion object {
        private const val ITEM_ADD_REQUEST_CODE = 3

    }
}
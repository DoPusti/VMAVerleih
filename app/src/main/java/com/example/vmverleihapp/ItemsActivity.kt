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
import com.example.vmverleihapp.utils.SwipeToDeleteCallback
import com.example.vmverleihapp.utils.SwipeToEditCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_items.*
import java.lang.NullPointerException

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
                        try {
                            val userID = userSnapshot.getValue(UserAuth::class.java)
                            if (userID!!.mail == firebaseAuth.currentUser!!.email.toString()) {
                                val user = User(userID.name, userID.description, userID.status, userID.imgUri, userID.userId)
                                userArrayList.add(user)
                            }

                        }catch (e : NullPointerException) {
                            Log.e("GetUserData", "Keine Daten vorhanden")
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
        val itemAdapter = MyAdapter(this,itemList)
        userRecyclerView.adapter = itemAdapter
        itemAdapter.setOnClickListener(object: MyAdapter.OnClickListener{
            override fun onClick(position: Int, model: User) {
                Log.i("OnCliCk",itemList[position].name.toString())
                val intent = Intent(this@ItemsActivity,ItemDetailActivity::class.java)
                intent.putExtra(ITEM_DETAIL_NAME,itemList[position].name.toString())
                intent.putExtra(ITEM_DETAIL_DESC,itemList[position].description.toString())
                intent.putExtra(ITEM_DETAIL_STATUS,itemList[position].status.toString())
                intent.putExtra(ITEM_DETAIL_IMGURI,itemList[position].imgUri.toString())
                startActivity(intent)
            }
        })


        val editSwipeHandler = object  : SwipeToEditCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val intent = Intent(this@ItemsActivity,ItemDetailActivity::class.java)

                intent.putExtra(ITEM_DETAIL_NAME,itemList[viewHolder.adapterPosition].name.toString())
                intent.putExtra(ITEM_DETAIL_DESC,itemList[viewHolder.adapterPosition].description.toString())
                intent.putExtra(ITEM_DETAIL_STATUS,itemList[viewHolder.adapterPosition].status.toString())
                intent.putExtra(ITEM_DETAIL_IMGURI,itemList[viewHolder.adapterPosition].imgUri.toString())
                startActivityForResult(intent, ITEM_EDIT_REQUEST_CODE)
            }

        }
        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(userRecyclerView)



        val deleteSwipeHandler = object  : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = userRecyclerView.adapter as MyAdapter
                adapter.removeAt(viewHolder.adapterPosition)

                getUserData()
            }

        }
        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(userRecyclerView)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == ITEM_ADD_REQUEST_CODE) {
                getUserData()
            }
            if (requestCode == ITEM_EDIT_REQUEST_CODE) {
                getUserData()
            }else {
                Log.e("Activity", "Abgebrochen oder zurück gedrückt")
            }
        }
    }

    companion object {
        private const val ITEM_ADD_REQUEST_CODE = 3
        private const val ITEM_DETAIL_REQUEST_CODE = 4
        private const val ITEM_DETAIL_NAME = "ITEM_DETAIL_NAME"
        private const val ITEM_DETAIL_DESC = "ITEM_DETAIL_DESC"
        private const val ITEM_DETAIL_STATUS = "ITEM_DETAIL_STATUS"
        private const val ITEM_DETAIL_IMGURI = "ITEM_DETAIL_IMGURI"
        private const val ITEM_EDIT_REQUEST_CODE = 5

    }
}
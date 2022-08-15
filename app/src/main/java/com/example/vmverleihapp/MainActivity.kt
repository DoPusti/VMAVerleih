package com.example.vmverleihapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vmverleihapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_items.*
import kotlinx.android.synthetic.main.activity_items.tvNoRecordsAvailable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var userRecyclerView: RecyclerView
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolBar)
        firebaseAuth = FirebaseAuth.getInstance()
        Log.i("UserMain", firebaseAuth.currentUser.toString())


        getAllData()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item?.itemId){
        R.id.user -> {
            val intent = Intent(this, EditProfileActivity::class.java)
            @Suppress("DEPRECATION")
            startActivityForResult(intent, EDIT_PROFILE_ACTIVITY_REQUEST_CODE)}

        R.id.chats -> {
            val intent = Intent(this, ChatsActivity::class.java)
            @Suppress("DEPRECATION")
            startActivity(intent)}
        }


        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getAllData()
            } else {
                Log.e("Activity", "Abgebrochen oder zurück gedrückt")
            }
        }


    }

    private fun getAllData() {

        userArrayList = ArrayList<User>()
        dbref =
            FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("Users")

        dbref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        if (user != null) {
                            userArrayList.add(user)
                        }

                    }
                }
                if(userArrayList.size > 0) {
                    allList.visibility = View.VISIBLE
                    tvNoRecordsAvailable.visibility = View.GONE
                    setupItemRecyclerView(userArrayList)
                } else {
                    allList.visibility = View.GONE
                    tvNoRecordsAvailable.visibility = View.VISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun setupItemRecyclerView(itemList: ArrayList<User>) {
        userRecyclerView = findViewById(R.id.allList)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf()
        val itemAdapter = MyAdapter(this,itemList)
        userRecyclerView.adapter = MyAdapter(this,itemList)

        itemAdapter.setOnClickListener(object : MyAdapter.OnClickListener {
            override fun onClick(position: Int, model: User) {
                val intent = Intent(this@MainActivity, ItemDetailActivity::class.java)
                intent.putExtra(ITEM_DETAIL_NAME,model.name.toString())
                intent.putExtra(ITEM_DETAIL_DESC,model.description.toString())
                intent.putExtra(ITEM_DETAIL_STATUS,model.status.toString())
                intent.putExtra(ITEM_DETAIL_IMGURI,model.imgUri.toString())
                Log.i("Adapter","wird gestartet")
                startActivity(intent)
            }
        })
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

    companion object {
        private const val EDIT_PROFILE_ACTIVITY_REQUEST_CODE = 1
        private const val ITEM_DETAIL_NAME = "ITEM_DETAIL_NAME"
        private const val ITEM_DETAIL_DESC = "ITEM_DETAIL_DESC"
        private const val ITEM_DETAIL_STATUS = "ITEM_DETAIL_STATUS"
        private const val ITEM_DETAIL_IMGURI = "ITEM_DETAIL_IMGURI"

    }
}
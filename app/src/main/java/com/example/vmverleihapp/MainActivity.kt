package com.example.vmverleihapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vmverleihapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_items.tvNoRecordsAvailable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var dbref: DatabaseReference
    private lateinit var userArrayList: ArrayList<User>
    private lateinit var userRecyclerView: RecyclerView
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var messagesIcon: MenuItem
    lateinit var adapter: MyAdapter
    private lateinit var searchMenuItem: SearchView

    private var latestMessagesHashMap = HashMap<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.mainToolBar)
        firebaseAuth = FirebaseAuth.getInstance()
        Log.i("UserMain", firebaseAuth.currentUser.toString())

        getAllData("")
        listenForLatestMessages()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)

        messagesIcon = menu?.children?.toList()?.get(1)!!
        var searchIcon = menu?.children?.toList()?.get(0)!!
        searchMenuItem = searchIcon.actionView as SearchView

        searchMenuItem.setOnSearchClickListener(View.OnClickListener {
            // TODO ToolBar vertikal vergrößern & mögliche Filter einblenden
        })

        searchMenuItem.setOnCloseListener(SearchView.OnCloseListener {
            // TODO Filter abwählen und ToolBar vertikal verkleinern
            getAllData("")
            return@OnCloseListener false
        })


        searchMenuItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // TODO RecyclerView Filtern
                getAllData(newText)
                return false
            }
        })

        val latestMessagedRead = latestMessagesHashMap.values.toList()
        val predicate: (Boolean) -> Boolean = { !it }
        val anyUnreadMessages = latestMessagedRead.any(predicate)
        if (anyUnreadMessages) {
            messagesIcon.setIcon(R.drawable.ic_message_red_dot_black_24dp)
        } else {
            messagesIcon.setIcon(R.drawable.ic_message_black_24dp)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item?.itemId) {
            R.id.user -> {
                val intent = Intent(this, EditProfileActivity::class.java)
                @Suppress("DEPRECATION")
                startActivityForResult(intent, EDIT_PROFILE_ACTIVITY_REQUEST_CODE)
            }

            R.id.chats -> {
                val intent = Intent(this, ChatsActivity::class.java)
                @Suppress("DEPRECATION")
                startActivity(intent)
            }
        }


        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getAllData("")
            } else {
                Log.e("Activity", "Abgebrochen oder zurück gedrückt")
            }
        }


    }

    private fun getAllData(inText: String) {

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
                            if (inText.isNotEmpty()) {
                                if (user.name!!.contains(inText)) {
                                    userArrayList.add(user)
                                }
                            } else {
                                userArrayList.add(user)
                            }

                        }

                    }
                }
                if (userArrayList.size > 0) {
                    allList.visibility = View.VISIBLE
                    tvNoRecordsAvailable.visibility = View.GONE
                    setupItemRecyclerView(userArrayList, "", "")
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

    private fun refreshMessagesIcon() {
        if (!this::messagesIcon.isInitialized) {
            return
        }

        val latestMessagedRead = latestMessagesHashMap.values.toList()
        val predicate: (Boolean) -> Boolean = { !it }
        val anyUnreadMessages = latestMessagedRead.any(predicate)

        if (anyUnreadMessages) {
            messagesIcon.setIcon(R.drawable.ic_message_red_dot_black_24dp)
        } else {
            messagesIcon.setIcon(R.drawable.ic_message_black_24dp)
        }
    }

    private fun listenForLatestMessages() {

        val fromId = FirebaseAuth.getInstance().uid
        val ref =
            FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("LatestMessages/$fromId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                setMessage(snapshot.key!!, message)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                setMessage(snapshot.key!!, message)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                removeMessage(snapshot.key!!)
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

            private fun setMessage(id: String, message: ChatMessage?) {
                if (message != null) {
                    latestMessagesHashMap[id] = message.read
                    refreshMessagesIcon()
                }
            }

            private fun removeMessage(id: String) {
                if (latestMessagesHashMap.containsKey(id)) {
                    latestMessagesHashMap.remove(id)
                }
                refreshMessagesIcon()
            }

        })
    }


    private fun setupItemRecyclerView(
        itemList: ArrayList<User>,
        inSearchString: String,
        inTrigger: String
    ) {
        userRecyclerView = findViewById(R.id.allList)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf()
        val itemAdapter = MyAdapter(this, itemList)
        itemAdapter.filter.filter(inSearchString)
        userRecyclerView.adapter = itemAdapter


        itemAdapter.setOnClickListener(object : MyAdapter.OnClickListener {
            override fun onClick(position: Int, model: User) {
                Log.i("OnCliCk", itemList[position].name.toString())
                val intent = Intent(this@MainActivity, ItemInquiryActivity::class.java)
                intent.putExtra(ITEM_DETAIL_NAME, itemList[position].name.toString())
                intent.putExtra(ITEM_DETAIL_DESC, itemList[position].description.toString())
                intent.putExtra(ITEM_DETAIL_STATUS, itemList[position].status.toString())
                intent.putExtra(ITEM_DETAIL_IMGURI, itemList[position].imgUri.toString())
                intent.putExtra(ITEM_DETAIL_USERID, itemList[position].userid.toString())
                startActivity(intent)
            }
        })

    }

    companion object {
        private const val EDIT_PROFILE_ACTIVITY_REQUEST_CODE = 1
        private const val ITEM_DETAIL_NAME = "ITEM_DETAIL_NAME"
        private const val ITEM_DETAIL_DESC = "ITEM_DETAIL_DESC"
        private const val ITEM_DETAIL_STATUS = "ITEM_DETAIL_STATUS"
        private const val ITEM_DETAIL_IMGURI = "ITEM_DETAIL_IMGURI"
        private const val ITEM_DETAIL_USERID = "ITEM_DETAIL_USERID"

    }
}
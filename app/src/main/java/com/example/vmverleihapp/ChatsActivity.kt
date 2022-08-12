package com.example.vmverleihapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chats.*

class ChatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Chats"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {onBackPressed()}

        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(ChatItem())
        adapter.add(ChatItem())
        adapter.add(ChatItem())

        chatsView.adapter = adapter

        fetchChats()

    }

    private fun fetchChats(){
        val dbRef = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Chats")
        dbRef.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}

class ChatItem : Item<GroupieViewHolder>()
{
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_row
    }

}
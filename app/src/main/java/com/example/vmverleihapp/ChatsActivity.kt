package com.example.vmverleihapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

        fetchChats()

    }

    private fun fetchChats(){
        val dbRef = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Chats")
        dbRef.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                for (child in snapshot.children) {
                    val chat = child.getValue(Chat::class.java)
                    adapter.add(ChatItem())
                }
                chatsView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}

class Chat(val user1: String, val user2: String)
{
    constructor() : this("","")
}

class ChatItem : Item<GroupieViewHolder>()
{
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_row
    }

}
package com.example.vmverleihapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_chat_log.toolbar

class ChatLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        setSupportActionBar(toolbar)

        val chatUser = intent.getParcelableExtra<ChatUser>(ChatsActivity.USER_KEY)
        supportActionBar?.title = chatUser?.email
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {onBackPressed()}

        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(ChatFromItem(Chat("u1","u2")))
        adapter.add(ChatToItem(Chat("u1","u2")))

        recyclerview_chat_log.adapter = adapter
    }
}

class Chat(val user1: String, val user2: String)
{
    constructor() : this("","")
}

class ChatFromItem(val chatUser: Chat) : Item<GroupieViewHolder>()
{
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}

class ChatToItem(val chatUser: Chat) : Item<GroupieViewHolder>()
{
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}
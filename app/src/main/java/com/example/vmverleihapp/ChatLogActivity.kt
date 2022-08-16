package com.example.vmverleihapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    var toUser: ChatUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        setSupportActionBar(toolbar)

        toUser = intent.getParcelableExtra<ChatUser>(ChatsActivity.USER_KEY)
        supportActionBar?.title = toUser?.email
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {onBackPressed()}

        recyclerview_chat_log.adapter = adapter

        receiveMessage()

        send_button_chat_log.setOnClickListener{
            sendMessage()
        }
    }

    private fun receiveMessage(){

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.id

        val dbRef = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Messages/$fromId/$toId")

        dbRef.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)

                if (message != null){
                    val sdf = SimpleDateFormat("dd.MM.yy HH:mm")
                    val date = Date(message.timestamp * 1000)
                    val timestamp = sdf.format(date)

                    if (message.fromId == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatToItem(message.text, timestamp))
                    }
                    else
                    {
                        adapter.add(ChatFromItem(message.text, timestamp))
                    }

                    if(adapter.itemCount > 0){
                        recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun sendMessage(){
        val fromId = FirebaseAuth.getInstance().uid
        val chatUser = intent.getParcelableExtra<ChatUser>(ChatsActivity.USER_KEY)
        val toId = chatUser?.id

        if(fromId == null)
        { return }

        val fromReference = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Messages/$fromId/$toId").push()

        val message = ChatMessage(fromReference.key!!, message_chat_log.text.toString(), fromId, toId!!, System.currentTimeMillis() / 1000 )
        fromReference.setValue(message).addOnSuccessListener {
            message_chat_log.text.clear()
            recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
         }

        val toReference = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Messages/$toId/$fromId").push()
        toReference.setValue(message)
    }
}

class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String, val timestamp: Long){
    constructor() : this("","", "", "", -1)
}

class ChatFromItem(val text: String, private val timestamp: String) : Item<GroupieViewHolder>()
{
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_message_from.text = text
        viewHolder.itemView.chat_timestamp_from.text = timestamp
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}

class ChatToItem(val text: String, private val timestamp: String) : Item<GroupieViewHolder>()
{
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_message_to.text = text
        viewHolder.itemView.chat_timestamp_to.text = timestamp
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}
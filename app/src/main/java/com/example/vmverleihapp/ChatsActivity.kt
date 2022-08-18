package com.example.vmverleihapp

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_chats.*
import kotlinx.android.synthetic.main.activity_chats.toolbar
import kotlinx.android.synthetic.main.chat_user_row.view.*
import java.util.*

class ChatsActivity : AppCompatActivity() {

    val adapter = GroupAdapter<GroupieViewHolder>()
    val userHashMap : HashMap<String, ChatUser> = HashMap<String, ChatUser> ()
    val latestMessagesHashMap : HashMap<String, LatestMessageItem> = HashMap<String, LatestMessageItem> ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)

        setSupportActionBar(toolbar)
        supportActionBar?.title = "Chats"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {onBackPressed()}

        chats_latest_Messages.adapter = adapter

        adapter.setOnItemClickListener {  item, view ->
            val intent = Intent(view.context, ChatLogActivity::class.java)
            val chatUserItem = item as LatestMessageItem
            intent.putExtra(USER_KEY, chatUserItem.user)
            startActivity(intent)

            //finish()
        }


        getUserData()
        listenForLatestMessages()
        //fetchChats()

    }

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesHashMap.values.sortedByDescending { i -> i.timestamp }.forEach {
            adapter.add(it)
        }
    }

    private fun listenForLatestMessages(){

        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/").getReference("LatestMessages/$fromId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                setMessage(snapshot.key!!, message)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(ChatMessage::class.java)
                setMessage(snapshot.key!!, message)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

            private fun setMessage(id: String, message: ChatMessage?)
            {
                if (message != null){
                    val user = userHashMap[message.toId]
                    if (user != null){
                        var latestMessage = LatestMessageItem(message.text, message.timestamp,user)
                        latestMessagesHashMap[id] = latestMessage
                        refreshRecyclerViewMessages()
                    }
                }
            }

        })
    }

    private fun getUserData(){
        val dbRef = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Profile")
        dbRef.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                setUser(snapshot.getValue(ChatUser::class.java))
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                setUser(snapshot.getValue(ChatUser::class.java))
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                var user = snapshot.getValue(ChatUser::class.java)
                if (user != null && userHashMap.containsKey(user.id)){
                    userHashMap.remove(user.id)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

            private fun setUser(user: ChatUser?)
            {
                if (user != null){
                    userHashMap[user.id] = user
                    refreshRecyclerViewMessages()
                }
            }

        })
    }

    companion object {
        const val USER_KEY = "USER_KEY"
    }

}

@Parcelize
class ChatUser(val nachname: String, val imgUri: String, val id: String) : Parcelable
{
    constructor() : this("","", "")
}

class LatestMessageItem(val latestMessage: String, val timestamp: Long, val user: ChatUser) : Item<GroupieViewHolder>()
{
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_user_name.text = user.nachname
        viewHolder.itemView.chat_latest_Message.text = latestMessage
       // Picasso.get().load(chatUser.imgUri).into(viewHolder.itemView.chat_image)
    }

    override fun getLayout(): Int {
        return R.layout.chat_user_row
    }

}
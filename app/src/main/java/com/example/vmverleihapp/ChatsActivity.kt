package com.example.vmverleihapp
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_chats.*
import kotlinx.android.synthetic.main.chat_user_row.view.*

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
        val dbRef = FirebaseDatabase.getInstance("https://vmaverleihapp-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Profile")
        dbRef.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                /*
                val adapter = GroupAdapter<GroupieViewHolder>()
                for (child in snapshot.children) {
                    val chat = child.getValue(ChatUser::class.java)
                    if(chat != null)
                    {
                        adapter.add(ChatUserItem(chat))
                    }

                }


                adapter.setOnItemClickListener {  item, view ->
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    val chatUserItem = item as ChatUserItem
                    intent.putExtra(USER_KEY, chatUserItem.chatUser)
                    startActivity(intent)

                    //finish()
                }
                chatsView.adapter = adapter*/
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    companion object {
        const val USER_KEY = "USER_KEY"
    }

}
/*
@Parcelize
class ChatUser(val email: String, val imgUri: String, val id: String) : Parcelable
{
    constructor() : this("","", "")
}

class ChatUserItem(val chatUser: ChatUser) : Item<GroupieViewHolder>()
{
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_user_name.text = chatUser.email
       // Picasso.get().load(chatUser.imgUri).into(viewHolder.itemView.chat_image)
    }

    override fun getLayout(): Int {
        return R.layout.chat_user_row
    }

}



 */
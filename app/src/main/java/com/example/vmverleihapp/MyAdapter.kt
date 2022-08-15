package com.example.vmverleihapp

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import java.io.File

open class MyAdapter(private val context: Context, private val userList : ArrayList<User>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private var onClickListener : OnClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item,
            parent,false)
        return MyViewHolder(itemView)

    }
    // Adapter kann keine OnClicklistener haben, daher dieser Umweg
    fun setOnClickListener(onClickListener: OnClickListener) {
        Log.i("Adapter","Onclick")
        this.onClickListener = onClickListener
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = userList[position]
        if(holder is MyViewHolder) {
            holder.name.text = currentitem.name
            holder.beschreibung.text = currentitem.description
            holder.status.text = currentitem.status
            // Bild aus Storage holen
            val storageRef = FirebaseStorage.getInstance().reference.child("myImages/${currentitem.imgUri.toString()}")
            Log.i("IMAGE",storageRef.toString())
            val localFile = File.createTempFile("tempImage","jpg")
            storageRef.getFile(localFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                holder.imgUri.setImageBitmap(bitmap)
            }.addOnFailureListener{
                Log.e("Adapter", "Fehler beim Laden des Bildes$storageRef")
            }
            holder.itemView.setOnClickListener {
                if(onClickListener != null) {
                    onClickListener!!.onClick(position, currentitem)
                }
            }

        }



    }

    override fun getItemCount(): Int {

        return userList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val name : TextView = itemView.findViewById(R.id.tvname)
        val beschreibung : TextView = itemView.findViewById(R.id.tvDescription)
        val status : TextView = itemView.findViewById(R.id.tvStatus)
        val imgUri : ImageView = itemView.findViewById(R.id.tvimage)

    }
    interface OnClickListener {
        fun onClick(position: Int, model: User) {

        }
    }



}

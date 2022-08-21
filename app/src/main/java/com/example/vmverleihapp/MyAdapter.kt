package com.example.vmverleihapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import java.io.File

open class MyAdapter(private val context: Context, private val userList: ArrayList<User>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private var onClickListener: OnClickListener? = null

    var userFilterList = ArrayList<User>()

    init {
        userFilterList = userList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.user_item,
            parent, false
        )
        return MyViewHolder(itemView)

    }

    // Adapter kann keine OnClicklistener haben, daher dieser Umweg
    fun setOnClickListener(onClickListener: OnClickListener) {
        Log.i("Adapter", "Onclick")
        this.onClickListener = onClickListener
    }

    fun removeAt(position: Int) : Int{
        var itemRC = 0
        val itemDelete = DeleteItem(
            "",
            userList[position].name.toString(),
            userList[position].description.toString(),
            userList[position].status.toString(),
            userList[position].imgUri.toString()
        )
        itemRC = if(itemDelete.deleteItem() == 0) {
            0
        } else {
            100
        }
        return itemRC
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = userList[position]
        if (holder is MyViewHolder) {
            holder.name.text = currentitem.name
            holder.beschreibung.text = currentitem.description
            holder.status.text = currentitem.status
            // Bild aus Storage holen
            val storageRef =
                FirebaseStorage.getInstance().reference.child("myImages/${currentitem.imgUri.toString()}")
            Log.i("IMAGE", storageRef.toString())
            val localFile = File.createTempFile("tempImage", "jpg")
            storageRef.getFile(localFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                holder.imgUri.setImageBitmap(bitmap)
            }.addOnFailureListener {
                Log.e("Adapter", "Fehler beim Laden des Bildes$storageRef")
            }
            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, currentitem)
                }
            }

        }


    }

    override fun getItemCount(): Int {

        return userFilterList.size
    }



    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.tvname)
        val beschreibung: TextView = itemView.findViewById(R.id.tvDescription)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val imgUri: ImageView = itemView.findViewById(R.id.tvimage)

    }

    interface OnClickListener {
        fun onClick(position: Int, model: User) {

        }
    }
    /*
   // https://johncodeos.com/how-to-add-search-in-recyclerview-using-kotlin/
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    userFilterList = userList
                } else {
                    val resultList = ArrayList<String>()
                    for (row in userList) {
                        if (row.lowercase(Locale.ROOT)
                                .contains(charSearch.lowercase(Locale.ROOT))
                        ) {
                            resultList.add(row.toString())
                        }
                    }
                    userFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = userFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

            }

        }
    }

     */


}

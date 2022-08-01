package com.example.practiceproject.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceproject.Activity.ChatActivity
import com.example.practiceproject.Model.GroupModel
import com.example.practiceproject.R
import com.google.firebase.auth.FirebaseAuth

class GroupAdapter(val context: Context,val arrayList: ArrayList<GroupModel>) : RecyclerView.Adapter<GroupAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.group_rv_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentUser = arrayList[position]
//        holder.image.setImageResource(currentUser.image)
        holder.name.text = currentUser.name

        holder.itemView.setOnClickListener{


//            val firebase = FirebaseAuth.getInstance()
//            val uid = firebase.currentUser?.uid.toString()
          val  intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("username",currentUser.name.toString())
            intent.putExtra("uid",currentUser.uid)
//            intent.putExtras(bundle)
             context.startActivity(intent)

//            Log.d("onClick",""+currentItem.heading)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val image:ImageView = itemView.findViewById(R.id.chat_rv_imageview)
        val name:TextView = itemView.findViewById(R.id.chat_rv_textview)

    }
}
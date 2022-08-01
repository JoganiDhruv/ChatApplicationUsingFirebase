package com.example.practiceproject.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practiceproject.Models.ChatModel
import com.example.practiceproject.R
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(var context: Context,var list:ArrayList<ChatModel>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SENT_ITEM = 1
    private val RECEIVE_ITEM = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
         if (viewType == 1) {

              val view : View =   LayoutInflater.from(context)
                                    .inflate(R.layout.sender_item, parent, false)
             return SenderViewHolder(view)

        }else {

            val view : View = LayoutInflater.from(context)
                                .inflate(R.layout.receiver_item, parent, false)
             return ReceiverViewHolder(view)

        }

    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = list[position]
         if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
             return SENT_ITEM
         } else{
             return RECEIVE_ITEM
         }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = list[position]

        if (holder.javaClass == SenderViewHolder::class.java){
            val viewHolder  = holder as SenderViewHolder
            if (message.type == "img" ){
                viewHolder.sendMessage.visibility = View.GONE
                viewHolder.sendImage.visibility = View.VISIBLE
                Glide.with(context)
                    .load(message.imageUrl!!.toUri())
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.sendImage)
//                Glide.with(holder.image.context).load(model.postImage).centerCrop().into(holder.image)
            }else{
                viewHolder.sendImage.visibility = View.GONE
                viewHolder.sendMessage.visibility = View.VISIBLE
                viewHolder.sendMessage.text = message.message
            }

        }else{
            val viewHolder  = holder as ReceiverViewHolder
            if (message.type == "img"){
                viewHolder.receiveMessage.visibility = View.GONE
                viewHolder.receiveImage.visibility = View.VISIBLE
                Glide.with(context)
                    .load(message.imageUrl!!.toUri())
//                    .load("gs://practice-project-619f0.appspot.com/planet-earth.png")
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.receiveImage)
//                Glide.with(holder.image.context).load(model.postImage).centerCrop().into(holder.image)

            }else{
                viewHolder.receiveImage.visibility = View.GONE
                viewHolder.receiveMessage.visibility = View.VISIBLE
                viewHolder.receiveMessage.text = message.message
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class SenderViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val sendMessage: TextView = itemView.findViewById(R.id.send_message)
        val sendImage : ImageView = itemView.findViewById(R.id.sender_image)

    }
    class ReceiverViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView = itemView.findViewById(R.id.receive_message)
        val receiveImage : ImageView = itemView.findViewById(R.id.receive_image)
    }
    }
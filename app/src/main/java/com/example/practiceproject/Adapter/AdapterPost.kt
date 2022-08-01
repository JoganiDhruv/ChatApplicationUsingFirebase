package com.example.practiceproject.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practiceproject.Models.PostModel
import com.example.practiceproject.R
import com.example.practiceproject.Utils
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AdapterPost(options: FirestoreRecyclerOptions<PostModel>, private val listener:IPostAdapter) :FirestoreRecyclerAdapter
                                                            <PostModel,AdapterPost.ViewHolder>(options) {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val postText : TextView = itemView.findViewById(R.id.postTitle)
        val userText : TextView = itemView.findViewById(R.id.userName)
        val createdAt : TextView = itemView.findViewById(R.id.createdAt)
        val likeCount : TextView = itemView.findViewById(R.id.likeCount)
//        val userImage : TextView = itemView.findViewById(R.id.userImage)
        val likeButton : ImageView = itemView.findViewById(R.id.likeButton)
        val image : ImageView = itemView.findViewById(R.id.postImage)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.post_item,parent,false))
        itemView.likeButton.setOnClickListener {
            listener.onClicked(snapshots.getSnapshot(itemView.adapterPosition).id)
        }
        return itemView
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: PostModel) {
        holder.postText.text = model.text
        holder.userText.text = model.createdBy.displayName
        //image pending
        holder.likeCount.text = model.likedBy.size.toString()
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)
//        holder.postImage.setImageURI(model.postImage)


        if (model.postImage==""){
            holder.image.visibility = View.GONE
        }else{
            holder.image.visibility = View.VISIBLE
            Glide.with(holder.image.context).load(model.postImage).centerCrop().into(holder.image)

        }


        val firebaseAuth = Firebase.auth
        val currentUserId = firebaseAuth.currentUser!!.uid
        val isLiked = model.likedBy.contains(currentUserId)

        if (isLiked){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context
                                                                                ,R.drawable.ic_like))
        }else{
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context
                                                                                 ,R.drawable.ic_unlike))
        }
    }

}

interface IPostAdapter{
    fun onClicked(postId:String){

    }
}
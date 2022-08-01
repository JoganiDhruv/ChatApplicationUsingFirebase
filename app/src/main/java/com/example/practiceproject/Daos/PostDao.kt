package com.example.practiceproject.Daos

import com.example.practiceproject.Models.PostModel
import com.example.practiceproject.Models.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {
    private val database = FirebaseFirestore.getInstance()
    val postCollections = database.collection("posts")
    private val firebaseAuth = Firebase.auth

    fun addPost(text :String,imageUri :String){
        val currentUserId = firebaseAuth.currentUser!!.uid
        GlobalScope.launch(Dispatchers.IO) {
            val userDao = UserDao()
            val user = userDao.getUserById(currentUserId).await().toObject(UserModel::class.java)!!

            val currentTime = System.currentTimeMillis()
            val post = PostModel(text,user,currentTime,imageUri)
            postCollections.document().set(post)

        }
    }

    fun getPostById(postId:String):Task<DocumentSnapshot>{
        return postCollections.document(postId).get()
    }

    fun updateLikes(postId:String){
        GlobalScope.launch {
            val currentUseId = firebaseAuth.currentUser!!.uid
            val post = getPostById(postId).await().toObject(PostModel::class.java)!!
            val isLiked = post.likedBy.contains(currentUseId)

            if (isLiked){
                post.likedBy.remove(currentUseId)
            }else{
                post.likedBy.add(currentUseId)
            }
            postCollections.document(postId).set(post)
        }
    }
}
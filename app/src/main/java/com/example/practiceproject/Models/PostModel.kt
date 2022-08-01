package com.example.practiceproject.Models


data class PostModel(
    var text :String = "",
    var createdBy:UserModel = UserModel(),
    var createdAt:Long = 0L,
    var postImage : String = "",
    var likedBy : ArrayList<String> = ArrayList()

)

package com.example.practiceproject.Models

data class ChatModel(
    var message : String? = "",
    var senderId : String? = "",
    var imageUrl : String? = null,
    val type :String = ""
)

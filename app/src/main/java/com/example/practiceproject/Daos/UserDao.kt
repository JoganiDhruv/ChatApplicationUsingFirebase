package com.example.practiceproject.Daos

import com.example.practiceproject.Models.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

 class UserDao {

    private val database = FirebaseFirestore.getInstance()
    private val usersCollection = database.collection("users")

     fun addUser (user : UserModel?){
         user?.let {
             GlobalScope.launch(Dispatchers.IO) {
                 usersCollection.document(user.uid).set(it)

             }
         }
     }

     fun getUserById(uId:String): Task<DocumentSnapshot>{
         return usersCollection.document(uId).get()

     }
}
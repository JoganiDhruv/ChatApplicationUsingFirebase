package com.example.practiceproject.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceproject.Adapter.ChatAdapter
import com.example.practiceproject.Models.ChatModel
import com.example.practiceproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var userName: TextView
    private lateinit var messageEditText: EditText
    private lateinit var send: ImageView

    private lateinit var senderUid: String
    private lateinit var receiverUid: String

    private var senderRoom: String? = null
    private var receiverRoom: String? = null

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: DatabaseReference
    private var storage: FirebaseStorage? = null
    private lateinit var storageReference : StorageReference

    private lateinit var list: ArrayList<ChatModel>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter

    private lateinit var attachFile: ImageView
    private lateinit var camera: ImageView
    private lateinit var relativeLayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        userName = findViewById(R.id.chat_userName)
        send = findViewById(R.id.chat_send_icon)
        messageEditText = findViewById(R.id.chat_editText)
        recyclerView = findViewById(R.id.chat_screen_recyclerview)
        backButton = findViewById(R.id.chat_backButton)
        attachFile = findViewById(R.id.attach_file)
        camera = findViewById(R.id.camera)
        relativeLayout = findViewById(R.id.container_fragment)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance().getReference()
        storageReference = FirebaseStorage.getInstance().getReference("uploads")
        storage = FirebaseStorage.getInstance()

        camera.setOnClickListener {

            val mapFragment = MapFragment()
            val fragment : Fragment? = supportFragmentManager.findFragmentByTag(MapFragment::class.java.simpleName)

            if (fragment !is MapFragment){
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container_fragment,mapFragment,MapFragment::class.java.simpleName)
                    .commit()
                relativeLayout.visibility = View.GONE
            }

        }

        storageReference.child("uploads/androidparty.png").downloadUrl.addOnSuccessListener {
            // Got the download URL for 'users/me/profile.png'

            Toast.makeText(this,"DOWNLOAD SUCCESS",Toast.LENGTH_SHORT).show()

        }.addOnFailureListener {
            // Handle any errors
            Toast.makeText(this,"DOWNLOAD FAIL",Toast.LENGTH_SHORT).show()

        }



//        // Create a storage reference from our app
//        val storageRef = storage!!.reference
//
//// Create a reference with an initial file path and name
//        val pathReference = storageRef.child("uploads")
//
//// Create a reference to a file from a Google Cloud Storage URI
//        val gsReference = storage.getReferenceFromUrl("uploads/androidparty.png").downloadUrl.addOnSuccessListener{
//            Toast.makeText(this,"DOWNLOAD SUCCESS",Toast.LENGTH_SHORT).show()
//        }.addOnFailureListener{
//            Toast.makeText(this,"DOWNLOAD FAIL",Toast.LENGTH_SHORT).show()
//        }

// Create a reference from an HTTPS URL
// Note that in the URL, characters are URL escaped!
//        val httpsReference = storage!!.getReferenceFromUrl(
//            "https://firebasestorage.googleapis.com/b/bucket/o/images%20stars.jpg")






        //sender ID & receiver ID
        senderUid = firebaseAuth.currentUser?.uid.toString()
        receiverUid = intent.getStringExtra("uid")!!
        val username = intent.getStringExtra("username").toString()
        userName.text = username


        //sender & receiver Room
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        //Attach file click event
        attachFile.setOnClickListener {

            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 25)
        }

        //Recycler View
        list = ArrayList()
        adapter = ChatAdapter(this, list)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        //Firebase data set
        firebaseDatabase.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    list.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(ChatModel::class.java)
                        list.add(message!!)
                    }
                    adapter.notifyDataSetChanged()

                }
                override fun onCancelled(error: DatabaseError) {

                }
            })

        //Send button Click event
        send.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            val messageObject = ChatModel(message, senderUid, type = "msg")

            if (message == "") {
                messageEditText.setError("Enter message")
            } else {
                //Store chat message into firebase realtime database
                firebaseDatabase.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        firebaseDatabase.child("chats").child(receiverRoom!!).child("messages")
                            .push()
                            .setValue(messageObject)
                    }
                messageEditText.setText("")
            }

        }

        //On back press
        backButton.setOnClickListener { onBackPressed() }

    }


    //After selecting image from gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 25) {
//            Toast.makeText(this,"IMAGE SENT ",Toast.LENGTH_SHORT).show()
            if (data != null) {
                if (data.data != null) {
                    //store image uri into selectedImage
                    val selectedImage = data.data


                    //For store image into firebase storage
                    val fileReference : StorageReference = storageReference.child(""+System.currentTimeMillis()+"."+getFileExtension(selectedImage!!))

                    fileReference.putFile(selectedImage!!).addOnSuccessListener {
                        Toast.makeText(this,"SUCCESS",Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {e->
                        Toast.makeText(this,"ON FAIL"+e.message,Toast.LENGTH_SHORT).show()
                        Log.d("image",""+e.message)
                    }.addOnProgressListener {
                        Toast.makeText(this,"ON PROGRESS",Toast.LENGTH_SHORT).show()
                    }

                    //To store chat image into firebase realtime database
                    val message = messageEditText.text.toString()
                    val messageObject = ChatModel(message, senderUid,selectedImage.toString(),"img")

                    firebaseDatabase.child("chats").child(senderRoom!!).child("messages").push()
                        .setValue(messageObject).addOnSuccessListener {
                            firebaseDatabase.child("chats").child(receiverRoom!!).child("messages")
                                .push()
                                .setValue(messageObject).addOnSuccessListener {
                                    Toast.makeText(this, "IMAGE SENT SUCCESSFUL", Toast.LENGTH_SHORT).show()
                                }
                        }

//                    firebaseDatabase = FirebaseDatabase.getInstance().getReference("uploads")
//                    firebaseDatabase.addValueEventListener(object :ValueEventListener{
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            for(postSnapshot in snapshot.children){
//                                val image = postSnapshot.getValue(ChatModel::class.java)
//                                list.add(image!!)
//                            }
//                            adapter.notifyDataSetChanged()
//
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//
//                        }
//
//                    })


                }
            }
        }
    }

   private fun getFileExtension(uri:Uri):String{
       val contentResolver = getContentResolver()
       val mime = MimeTypeMap.getSingleton()
       return mime.getExtensionFromMimeType(contentResolver.getType(uri)).toString()

    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == 25){
////            Toast.makeText(this,"IMAGE SENT ",Toast.LENGTH_SHORT).show()
//            if (data != null){
//                if (data.data != null){
//                    val selectedImage = data.data
////                    Log.d("image",""+selectedImage.toString())
//                    val calender = Calendar.getInstance()
//                    val reference = storage!!.reference.child("chats")
//                        .child(calender.timeInMillis.toString()+"")
//                        reference.putFile(selectedImage!!)
//                            .addOnCompleteListener{ task ->
//                                if (task.isSuccessful){
//                                    Toast.makeText(this,"IMAGE SENT SUCCESSFUL",Toast.LENGTH_SHORT).show()
//                                    reference.downloadUrl.addOnSuccessListener { uri ->
//                                        val filePath = uri.toString()
//                                        val messageTxt :String = messageEditText.text.toString()
//                                        val message = ChatModel(messageTxt,senderUid)
//                                        message.message = "photo"
//                                        message.imageUrl = filePath
//                                        messageEditText.setText("")
//                                        val randomkey = firebaseDatabase.ref.push().key
//                                        val lastMsgObj = HashMap<String,Any>()
//                                        lastMsgObj["lastMsg"] = message.message!!
//                                        firebaseDatabase!!.ref.child("chats")
//                                            .updateChildren(lastMsgObj)
//                                        firebaseDatabase!!.ref.child("chats")
//                                            .child(receiverRoom!!)
//                                            .updateChildren(lastMsgObj)
//                                        firebaseDatabase!!.ref.child("chats")
//                                            .child(senderRoom!!)
//                                            .updateChildren(lastMsgObj)
//                                        firebaseDatabase!!.ref.child("chats")
//                                            .child(senderRoom!!)
//                                            .child("message")
//                                            .child(randomkey!!)
//                                            .setValue(message).addOnSuccessListener {
//                                                firebaseDatabase!!.ref.child("chats")
//                                                    .child(receiverRoom!!)
//                                                    .child("message")
//                                                    .child(randomkey!!)
//                                                    .setValue(message).addOnSuccessListener {
//
//                                                    }
//                                            }
//
//                                    }
//                                }
//                                else{
//                                    Toast.makeText(this,"IMAGE SENT  NOT SUCCESSFUL",Toast.LENGTH_SHORT).show()
//
//                                }
//                            }
//                        }
//                     }
//                }
//    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK && requestCode == pickImage)
//        {
//            imageUri = data?.data
//            image.setImageURI(imageUri)
////            Log.d("ImageURI",""+imageUri)
//        }
//    }

}


//send.setOnClickListener{
//    if (messageEditText.text.isEmpty()){
//        messageEditText.setError("Enter message")
//    }else{
//
//        val message  = MessageModel(messageEditText.text.toString(),senderUid, Date().time)
//        val randomKey = firebaseDatabase.reference.push().key
//
//        firebaseDatabase.reference.child("chats")
//            .child(senderRoom!!).child("message").child(randomKey!!).setValue(message).addOnSuccessListener {
//
//
//                messageEditText.text = null
//                Toast.makeText(this,"Message sent !!",Toast.LENGTH_SHORT).show()
//
//            }
//    }
//}


//firebaseDatabase.reference.child("chats").child(senderRoom!!).child("message")
//.addValueEventListener(object :ValueEventListener{
//    override fun onDataChange(snapshot: DataSnapshot) {
//        list.clear()
//
//        for (snapshot1 in snapshot.children){
//            val data = snapshot1.getValue(MessageModel::class.java)
//            list.add(data!!)
//        }
//
//        recyclerView.adapter = ChatAdapter(this@ChatActivity,list)
//
//    }
//
//    override fun onCancelled(error: DatabaseError) {
//
//        Toast.makeText(this@ChatActivity,"Error :$error",Toast.LENGTH_SHORT).show()
//    }
//
//})
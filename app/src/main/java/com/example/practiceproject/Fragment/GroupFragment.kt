package com.example.practiceproject.Fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceproject.Adapter.GroupAdapter
import com.example.practiceproject.Model.GroupModel
import com.example.practiceproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class GroupFragment : Fragment() {

    private lateinit var adapter : GroupAdapter
    private lateinit var recyclerView: RecyclerView

//    lateinit var images :Array<Int>
   private lateinit var arraylist : ArrayList<GroupModel>

   private lateinit var firebaseAuth: FirebaseAuth
   private lateinit var databaseRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        dataInitialize()

        firebaseAuth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().getReference()
        recyclerView = view.findViewById(R.id.chat_recyclerView)
        arraylist = ArrayList()
        val layoutManager = LinearLayoutManager(context)

        adapter = GroupAdapter(requireContext(),arraylist)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter


        databaseRef.child("user").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                arraylist.clear()
                for (postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(GroupModel::class.java)
                    if (firebaseAuth.currentUser?.uid!= currentUser?.uid){
                        arraylist.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }




}
package com.example.practiceproject.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceproject.Activity.CreatePostActivity
import com.example.practiceproject.Adapter.AdapterPost
import com.example.practiceproject.Adapter.IPostAdapter
import com.example.practiceproject.Daos.PostDao
import com.example.practiceproject.Models.PostModel
import com.example.practiceproject.R
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.Query

class HomeFragment : Fragment(), IPostAdapter {
    lateinit var floatingButton : FloatingActionButton
//    lateinit var arraylist : ArrayList<PostModel>
//    lateinit var adapter: PostAdapter
    lateinit var recyclerView: RecyclerView

    private lateinit var adapter: AdapterPost
    private lateinit var postDao: PostDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.post_recyclerview)

        postDao = PostDao()
//        setRecyclerView()
        setRecyclerView()


        floatingButton = view.findViewById(R.id.floatingActionButton)
        floatingButton.setOnClickListener {
            val intent = Intent(activity, CreatePostActivity::class.java)
            activity?.startActivity(intent)

        }
            return view
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()

    }

//    override fun onStop() {
//        super.onStop()
//        adapter.stopListening()
//
//    }


    private fun setRecyclerView() {
        val postCollection = postDao.postCollections
        val qurey = postCollection.orderBy("createdAt",Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<PostModel>().setQuery(qurey,PostModel::class.java).build()

        adapter = AdapterPost(recyclerViewOptions,this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

    }



    override fun onClicked(postId: String) {
        super.onClicked(postId)

        postDao.updateLikes(postId)
    }

}
package com.example.practiceproject.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.example.practiceproject.Adapter.ViewPagerAdapter
import com.example.practiceproject.Fragment.GroupFragment
import com.example.practiceproject.Fragment.HomeFragment
import com.example.practiceproject.Fragment.UserFragment
import com.example.practiceproject.R
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var viewpager :ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewpager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
        setUpFragment()
    }

    private fun setUpFragment() {
         val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(HomeFragment(),"Home")
        adapter.addFragment(GroupFragment(),"Group")
        adapter.addFragment(UserFragment(),"User")
        viewpager.adapter = adapter
        tabLayout.setupWithViewPager(viewpager)

        //example for set icon in fragment
        tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_baseline_home_24)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_baseline_group_24)
        tabLayout.getTabAt(2)!!.setIcon(R.drawable.ic_baseline_person_24)
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){

        }else{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
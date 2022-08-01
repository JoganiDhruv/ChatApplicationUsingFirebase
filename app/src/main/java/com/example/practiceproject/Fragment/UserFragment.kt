package com.example.practiceproject.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.practiceproject.Activity.MainActivity
import com.example.practiceproject.R
import com.google.firebase.auth.FirebaseAuth

class UserFragment : Fragment() {

    lateinit var signOut : Button
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var email : TextView

    private lateinit var phoneText : TextView
    private lateinit var phoneEdit : EditText
    private lateinit var phoneEditImg : ImageView
    private lateinit var phoneEditDone : ImageView

    private lateinit var addressText : TextView
    private lateinit var addressEdit : EditText
    private lateinit var addressEditImg : ImageView
    private lateinit var addressEditDone : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_user, container, false)

        phoneText = view.findViewById(R.id.profile_phone_text)
        phoneEdit = view.findViewById(R.id.profile_phone_editText)
        phoneEditImg = view.findViewById(R.id.edit_phone)
        phoneEditDone = view.findViewById(R.id.edit_phone_done)

        addressText = view.findViewById(R.id.profile_address_text)
        addressEdit = view.findViewById(R.id.profile_address_editText)
        addressEditImg = view.findViewById(R.id.edit_address)
        addressEditDone = view.findViewById(R.id.edit_address_done)


        phoneText.visibility = View.VISIBLE
        phoneEdit.visibility = View.GONE
        addressText.visibility = View.VISIBLE
        addressEdit.visibility = View.GONE

        phoneEditImg.setOnClickListener{
            phoneText.visibility = View.GONE
            phoneEdit.visibility = View.VISIBLE
            phoneEditImg.visibility = View.GONE
            phoneEditDone.visibility = View.VISIBLE
        }

        phoneEditDone.setOnClickListener {
            phoneText.text = phoneEdit.text
            phoneText.visibility = View.VISIBLE
            phoneEdit.visibility = View.GONE
            phoneEditImg.visibility = View.VISIBLE
            phoneEditDone.visibility = View.GONE
        }

        addressEditImg.setOnClickListener{
            addressText.visibility = View.GONE
            addressEdit.visibility = View.VISIBLE
            addressEditImg.visibility = View.GONE
            addressEditDone.visibility = View.VISIBLE
        }

        addressEditDone.setOnClickListener {
            addressText.text = addressEdit.text
            addressText.visibility = View.VISIBLE
            addressEdit.visibility = View.GONE
            addressEditImg.visibility = View.VISIBLE
            addressEditDone.visibility = View.GONE
        }

        firebaseAuth = FirebaseAuth.getInstance()

        email = view.findViewById(R.id.profile_email_text)

        val firebaseUser = firebaseAuth.currentUser
        val userEmail = firebaseUser!!.email
        val username = firebaseUser!!.displayName
        email.text= userEmail.toString()
//        Toast.makeText(activity,""+userEmail,Toast.LENGTH_SHORT).show()
//        Toast.makeText(activity,""+username, Toast.LENGTH_SHORT).show()

        //SignOut Code
        signOut = view.findViewById(R.id.home_signOutBtn)
        signOut.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }

        return view
    }


    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null){

        }else{
            val intent = Intent (activity, MainActivity::class.java)
            activity?.startActivity(intent)
        }
    }

}
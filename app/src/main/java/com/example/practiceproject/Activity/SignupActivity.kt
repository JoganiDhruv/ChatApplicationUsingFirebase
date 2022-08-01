package com.example.practiceproject.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.WindowManager
import android.widget.*
import com.example.practiceproject.Daos.UserDao
import com.example.practiceproject.Model.GroupModel
import com.example.practiceproject.Models.UserModel
import com.example.practiceproject.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {
    //    private lateinit var emailInput : TextInputLayout
//    private lateinit var userInput : TextInputLayout
//    private lateinit var passwordInput : TextInputLayout
    private lateinit var signupButton: Button
    private lateinit var loginText: TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var usernameEdittext: EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseRef : DatabaseReference


    private  lateinit var signUpWithGoogle : RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        loginText = findViewById(R.id.login_textView)
        signupButton = findViewById(R.id.SignupButton)
        emailEditText = findViewById(R.id.signup_email)
        passwordEditText = findViewById(R.id.signup_password)
        usernameEdittext = findViewById(R.id.signup_username)

        signUpWithGoogle = findViewById(R.id.signup_with_google_rl)

        firebaseAuth= FirebaseAuth.getInstance()

        signupButton.setOnClickListener {
            signup()
        }

        loginText.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

//        signInRequest = BeginSignInRequest.builder()
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    // Your server's client ID, not your Android client ID.
//                    .setServerClientId(getString(R.string.your_web_client_id))
//                    // Only show accounts previously used to sign in.
//                    .setFilterByAuthorizedAccounts(true)
//                    .build())
//            .build()
    }

    private fun signup() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val username = usernameEdittext.text.toString().trim()


        if (username.equals("")) {
            usernameEdittext.setError("Enter username")
        } else if (email.equals("")) {
            emailEditText.setError("Enter email")

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Invalid email format")

        } else if (password.equals("")) {
            passwordEditText.setError("Enter password")
        } else {
            firebaseSignup(email,password,username)
            addUserInFirebase()
        }
    }

    private fun addUserInFirebase() {

    }

    private fun firebaseSignup(email: String, password: String,username:String) {


        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {

                val firebaseUser = firebaseAuth.currentUser

                val userEmail = firebaseUser!!.email
                Toast.makeText(this,"Signup as $userEmail", Toast.LENGTH_SHORT).show()

                val user = firebaseUser?.let { UserModel(it.uid,firebaseUser.displayName) }
//                                                        firebaseUser.photoUrl.toString()
                val userDao = UserDao()
                userDao.addUser(user)

                addUserToDatabase(username,email, firebaseUser.uid)
                startActivity(Intent(this, HomeActivity::class.java))
                finish()

            }
            .addOnFailureListener {e ->
                Toast.makeText(this,"Signup  failed due to  ${e.message}",Toast.LENGTH_SHORT).show()

            }
    }

    private fun addUserToDatabase(username: String, email: String, uid: String) {
        databaseRef = FirebaseDatabase.getInstance().getReference()

        databaseRef.child("user").child(uid).setValue(GroupModel(username,email,uid))

    }

    override fun onBackPressed() {
//        super.onBackPressed()
        finishAffinity()
    }
}
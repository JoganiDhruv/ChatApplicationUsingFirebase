package com.example.practiceproject.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.practiceproject.Daos.UserDao
import com.example.practiceproject.Models.UserModel
import com.example.practiceproject.R
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

//    private lateinit var emailInput :TextInputLayout
//    private lateinit var passwordInput :TextInputLayout
    private lateinit var loginButton : Button
    private lateinit var signupText : TextView
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        signupText = findViewById(R.id.signup_textView)
        loginButton = findViewById(R.id.loginButton)
        emailEditText = findViewById(R.id.login_email)
        passwordEditText = findViewById(R.id.login_password)


        loginButton.setOnClickListener {
            login()
        }

        signupText.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        //Firebase
        firebaseAuth= FirebaseAuth.getInstance()

    }

    override fun onStart() {
        super.onStart()
        checkUser()
    }

    private fun checkUser() {
        val firebaseUser  = firebaseAuth.currentUser
        if (firebaseUser != null){
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }

    private fun login() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.equals("")){
            emailEditText.setError("Enter email")

        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEditText.setError("Invalid email format")

        } else if (password.equals("")){
            passwordEditText.setError("Enter password")

        }else {
            //function for firebaseLogin
            firebaseLogin(email,password)
        }
    }

    private fun firebaseLogin(email: String, password: String) {

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {

                val firebaseUser = firebaseAuth.currentUser
                val userEmail = firebaseUser!!.email
                Toast.makeText(this,"LoggedIn as $userEmail",Toast.LENGTH_SHORT).show()
                val user = firebaseUser?.let { UserModel(it.uid,firebaseUser.displayName) }
//                                                        firebaseUser.photoUrl.toString()
                val userDao = UserDao()
                userDao.addUser(user)
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this,"Login failed due to  ${e.message}",Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        finishAffinity()
    }
}
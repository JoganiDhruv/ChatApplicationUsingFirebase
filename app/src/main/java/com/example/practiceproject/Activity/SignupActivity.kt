package com.example.practiceproject.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.WindowManager
import android.widget.*
import com.example.practiceproject.Daos.UserDao
import com.example.practiceproject.Model.GroupModel
import com.example.practiceproject.Models.UserModel
import com.example.practiceproject.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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


    private val RC_SIGN_IN: Int = 123
    private val TAG = "SignInActivity Tag"
    private lateinit var googleSignInClient: GoogleSignInClient


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





        signUpWithGoogle.setOnClickListener{
            signIn()
        }

        loginText.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        firebaseAuth = Firebase.auth

    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        updateUI(currentUser)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        signInButton.visibility = View.GONE
//        progressBar.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.IO) {
            val auth = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main) {
                updateUI(firebaseUser)
            }
        }

    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if(firebaseUser != null) {

            val user = UserModel(firebaseUser.uid, firebaseUser.displayName, firebaseUser.photoUrl.toString())
            val usersDao = UserDao()
            usersDao.addUser(user)

            val mainActivityIntent = Intent(this, HomeActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        } else {
//            signInButton.visibility = View.VISIBLE
//            progressBar.visibility = View.GONE
        }
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
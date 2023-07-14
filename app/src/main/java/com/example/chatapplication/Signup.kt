package com.example.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.ArrayList

class Signup : AppCompatActivity() {

    private lateinit var edtName : EditText
    private lateinit var edtEmail : EditText
    private lateinit var edtPassword : EditText
    private lateinit var edtConfirmPassword : EditText
    private lateinit var btnSignup : Button
    private lateinit var mAuth : FirebaseAuth
    private lateinit var mDb : DatabaseReference

    private lateinit var userList : ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        supportActionBar?.hide()

        edtName = findViewById(R.id.name_txt)
        edtEmail = findViewById(R.id.email_txt)
        edtPassword = findViewById(R.id.password_txt)
        edtConfirmPassword = findViewById(R.id.confirm_password_txt)
        btnSignup = findViewById(R.id.signup_btn)

        userList = ArrayList()

        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseDatabase.getInstance().reference

        btnSignup.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            val confirmPassword = edtConfirmPassword.text.toString()

            mDb.child("users").addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (postSnapShot in snapshot.children) {
                        val currentUser = postSnapShot.getValue(User::class.java)
                        userList.add(currentUser!!)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
            var foundUser : String? = null
            userList.forEach { user ->
                if ( user.name == name ) {
                    foundUser = name
                }
            }

            if ( foundUser != null ) {
                Toast.makeText(this,"Username already exists!",Toast.LENGTH_SHORT).show()
            }
            else if ( password != confirmPassword ) {
                Toast.makeText(this,"Password doesn't match!",Toast.LENGTH_SHORT).show()
            }
            else {
                signup(name, email, password)
            }
        }
    }

    private fun signup(name: String,email : String,password : String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDatabase(name,email,mAuth.currentUser?.uid!!)
                    val intent = Intent(this,MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this,"Some error occured!",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(name: String,email: String,uid: String) {
        mDb = FirebaseDatabase.getInstance().getReference()
        mDb.child("users").child(uid).setValue(User(name,email,uid))
    }
}
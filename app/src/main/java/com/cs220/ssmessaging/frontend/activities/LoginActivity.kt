package com.cs220.ssmessaging.frontend.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.cs220.ssmessaging.R
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var homeToolbar: Toolbar
    private lateinit var username: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var newAccountLink: TextView
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        homeToolbar = findViewById(R.id.home_toolbar)
        setSupportActionBar(homeToolbar)

        username = findViewById(R.id.login_username)
        phoneNumber = findViewById(R.id.login_phone_number)
        newAccountLink = findViewById(R.id.need_to_register)
        loginButton = findViewById(R.id.login_button)

        newAccountLink.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }

        loginButton.setOnClickListener {
            val number = phoneNumber.text.toString().trim()
            val usernameText = username.text.toString()
            if (number.isEmpty() || number.length < 10) {
                Toast.makeText(this, "Please enter a valid phone number.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                usernameExists(usernameText, number)
            }
        }
    }

    override fun onBackPressed() {
        return
    }

    private fun usernameExists(
        usernameText: String,
        number: String
    ) {
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("canonicalId", usernameText)
            .get()
            .addOnSuccessListener { documentQuery ->
                if (documentQuery.size() == 1) {
                    val phoneNumber = documentQuery.documents[0].getString("phone")
                    val fullNumber = "+1$number"
                    if (phoneNumber == fullNumber) {
                        val firstName = documentQuery.documents[0].getString("first_name")
                        val lastName = documentQuery.documents[0].getString("last_name")
                        val authIntent = Intent(this, PhoneAuthActivity::class.java)
                        authIntent.putExtra("phonenumber", fullNumber)
                        authIntent.putExtra("firstname", firstName)
                        authIntent.putExtra("lastname", lastName)
                        authIntent.putExtra("username", usernameText)
                        authIntent.putExtra("newuser", false)
                        startActivity(authIntent)
                    } else {
                        Toast.makeText(
                            this,
                            "Username and phone number do not match. Try again or make an account.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Username is not found. Try again or make an account.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}

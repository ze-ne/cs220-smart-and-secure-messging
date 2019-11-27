package com.cs220.ssmessaging.frontend.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.User
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var homeToolbar: Toolbar
    private lateinit var username: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var firstname: EditText
    private lateinit var lastname: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        homeToolbar = findViewById(R.id.home_toolbar)
        setSupportActionBar(homeToolbar)

        username = findViewById(R.id.register_username)
        phoneNumber = findViewById(R.id.register_phone_number)
        firstname = findViewById(R.id.register_firstname)
        lastname = findViewById(R.id.register_lastname)
        registerButton = findViewById(R.id.register_button)

        registerButton.setOnClickListener {
            val number = phoneNumber.text.toString().trim()
            val firstnameText = firstname.text.toString()
            val lastnameText = lastname.text.toString()
            val usernameText = username.text.toString()

            if (validRegistration(firstnameText, lastnameText, usernameText, number)) {
                usernameExists(usernameText, firstnameText, lastnameText, number)
            } else {
                Toast.makeText(this, "Please enter valid information", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validRegistration(
        firstname: String,
        lastname: String,
        username: String,
        number: String
    ): Boolean {
        return (User.isValidName(firstname) && User.isValidName(lastname) && User.isValidUserId(
            username
        ) && number.isNotEmpty() && number.length == 10)
    }

    private fun usernameExists(
        usernameText: String,
        firstnameText: String,
        lastnameText: String,
        number: String
    ) {
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("canonicalId", usernameText)
            .get()
            .addOnSuccessListener { documentQuery ->
                if (documentQuery.size() == 0) {
                    val fullNumber = "+1$number"
                    val authIntent = Intent(this, PhoneAuthActivity::class.java)
                    authIntent.putExtra("phonenumber", fullNumber)
                    authIntent.putExtra("firstname", firstnameText)
                    authIntent.putExtra("lastname", lastnameText)
                    authIntent.putExtra("username", usernameText)
                    authIntent.putExtra("newuser", true)
                    startActivity(authIntent)
                } else {
                    Toast.makeText(this, "Username is already taken", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}
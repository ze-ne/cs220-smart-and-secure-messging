package com.cs220.ssmessaging.frontend.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cs220.ssmessaging.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var firstname: EditText
    private lateinit var lastname: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        username = findViewById(R.id.register_username)
        phoneNumber = findViewById(R.id.register_phone_number)
        firstname = findViewById(R.id.register_firstname)
        lastname = findViewById(R.id.register_lastname)
        registerButton = findViewById(R.id.register_button)

        registerButton.setOnClickListener{
            val number = phoneNumber.text.toString().trim()
            val firstnameText = firstname.text.toString()
            val lastnameText = lastname.text.toString()
            val usernameText = username.text.toString()

            // TODO: check for valid name/username
            // TODO: check that username isn't already taken
            if (number.isEmpty() || number.length < 10 || firstnameText.isEmpty() || lastnameText.isEmpty() || usernameText.isEmpty()  ) {
                Toast.makeText(this, "Please enter a valid information", Toast.LENGTH_SHORT).show()
            } else {

                val fullNumber = "+1$number"
                val authIntent = Intent(this, PhoneAuthActivity::class.java)
                authIntent.putExtra("phonenumber", fullNumber)
                authIntent.putExtra("firstname", firstnameText)
                authIntent.putExtra("lastname", lastnameText)
                authIntent.putExtra("username", usernameText)
                startActivity(authIntent)
            }
        }
    }
}
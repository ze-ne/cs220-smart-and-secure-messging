package com.cs220.ssmessaging.frontend.activities

import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.google.common.base.Strings.isNullOrEmpty
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.User

class SettingsActivity : AppCompatActivity() {
    private lateinit var homeToolbar: Toolbar
    private lateinit var usernameText: TextView
    private lateinit var firstNameField: EditText
    private lateinit var lastNameField: EditText
    private lateinit var changeFirstNameButton: Button
    private lateinit var changeLastNameButton: Button
    private lateinit var logoutButton: Button
    private lateinit var currentUser: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        currentUser = MyApplication.currentUser!!

        homeToolbar = findViewById(R.id.home_toolbar)
        homeToolbar.title = "Profile"
        setSupportActionBar(homeToolbar)

        usernameText = findViewById(R.id.username_text)
        firstNameField = findViewById(R.id.settings_firstName)
        lastNameField = findViewById(R.id.settings_lastName)

        usernameText.text = currentUser.userId
        firstNameField.hint = currentUser.firstName
        lastNameField.hint = currentUser.lastName

        changeFirstNameButton = findViewById(R.id.changeFirstNameButton)
        changeLastNameButton = findViewById(R.id.changeLastNameButton)
        logoutButton = findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener {
            currentUser.removeListeners()
            FirebaseAuth.getInstance().signOut()
            val registerIntent = Intent(this, LoginActivity::class.java)
            startActivity(registerIntent)
        }

        changeFirstNameButton.setOnClickListener {
            val firstName = firstNameField.text.toString()
            if (isNullOrEmpty(firstName)) {
                Toast.makeText(this, "First name change failed", Toast.LENGTH_SHORT).show()
            } else {
                if (User.isValidName(firstName)) {
                    currentUser.updateFirstName(firstName)
                    currentUser.firstName = firstName
                    firstNameField.hint = firstName
                    firstNameField.text.clear()
                    Toast.makeText(this, "First name change successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter a valid name.", Toast.LENGTH_SHORT).show()
                    firstNameField.text.clear()
                }
            }
        }

        changeLastNameButton.setOnClickListener {
            val lastName = lastNameField.text.toString()
            if (isNullOrEmpty(lastName)) {
                Toast.makeText(this, "Last name change failed", Toast.LENGTH_SHORT).show()
            } else {
                if (User.isValidName(lastName)) {
                    MyApplication.currentUser?.updateLastName(lastName)
                    MyApplication.currentUser?.lastName = lastName
                    lastNameField.hint = lastName
                    lastNameField.text.clear()
                    Toast.makeText(this, "Last name change successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter a valid name.", Toast.LENGTH_SHORT).show()
                    lastNameField.text.clear()
                }
            }
        }
    }
}
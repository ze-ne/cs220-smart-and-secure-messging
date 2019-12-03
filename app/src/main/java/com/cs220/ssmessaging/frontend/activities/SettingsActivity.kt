package com.cs220.ssmessaging.frontend.activities

import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.google.common.base.Strings.isNullOrEmpty
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.cs220.ssmessaging.MyApplication.MyApplication.Companion.currentUser
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.User

class SettingsActivity : AppCompatActivity() {
    private lateinit var homeToolbar: Toolbar
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
        setSupportActionBar(homeToolbar)

        firstNameField = findViewById(R.id.settings_firstName)
        lastNameField = findViewById(R.id.settings_lastName)

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
                Toast.makeText(this, "First Name Change Failed", Toast.LENGTH_SHORT).show()
            } else {
                currentUser.updateFirstName(firstName)
                currentUser.firstName = firstName
                firstNameField.hint = firstName
                firstNameField.text.clear()
                Toast.makeText(this, "First Name Change Successful", Toast.LENGTH_SHORT).show()
            }

        }

        changeLastNameButton.setOnClickListener {
            val lastName = lastNameField.text.toString()
            if (isNullOrEmpty(lastName)) {
                Toast.makeText(this, "Last Name Change Failed", Toast.LENGTH_SHORT).show()
            } else {
                MyApplication.currentUser?.updateLastName(lastName)
                MyApplication.currentUser?.lastName = lastName
                lastNameField.hint = lastName
                lastNameField.text.clear()
                Toast.makeText(this, "Last Name Change Successful", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
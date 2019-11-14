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
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.frontend.presenters.SettingsActivityPresenter

class SettingsActivity : AppCompatActivity(), SettingsActivityPresenter.View {
    private val presenter: SettingsActivityPresenter = SettingsActivityPresenter()
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var changeFirstNameButton: Button
    private lateinit var changeLastNameButton: Button
    private lateinit var logoutButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        firstName = (findViewById(R.id.settings_firstName)as EditText).text.toString()
        lastName = (findViewById(R.id.settings_lastName) as EditText).text.toString()

        changeFirstNameButton = findViewById<Button>(R.id.changeFirstNameButton)
        changeLastNameButton = findViewById<Button>(R.id.changeLastNameButton)
        logoutButton = findViewById<Button>(R.id.logoutButton)

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val registerIntent = Intent(this, LoginActivity::class.java)
            startActivity(registerIntent)

        }

        // TODO: does this change in the database as well??
        changeFirstNameButton.setOnClickListener {
            if (isNullOrEmpty (firstName))
                changeFirstNameFail()
            else
                MyApplication.currentUser?.firstName = firstName
            changeFirstNameSuccessful()
        }

        changeLastNameButton.setOnClickListener {
            if (isNullOrEmpty (lastName))
                changeLastNameFail()
            else
                MyApplication.currentUser?.lastName = lastName
            changeLastNameSuccessful()
        }
    }

    override fun logoutSuccessful() {
        Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show()
    }

    override fun logoutFail() {
        Toast.makeText(this, "Logout Failed", Toast.LENGTH_SHORT).show()
    }

    override fun changeFirstNameSuccessful() {
        Toast.makeText(this, "First Name Change Successful", Toast.LENGTH_SHORT).show()
    }

    override fun changeFirstNameFail() {
        Toast.makeText(this, "First Name Change Failed", Toast.LENGTH_SHORT).show()
    }

    override fun changeLastNameSuccessful() {
        Toast.makeText(this, "Last Name Change Successful", Toast.LENGTH_SHORT).show()
    }

    override fun changeLastNameFail() {
        Toast.makeText(this, "Last Name Change Failed", Toast.LENGTH_SHORT).show()
    }
}
package com.cs220.ssmessaging.frontend.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.User
import com.cs220.ssmessaging.frontend.presenters.LoginActivityPresenter

class LoginActivity : AppCompatActivity(), LoginActivityPresenter.View {
    private val presenter: LoginActivityPresenter = LoginActivityPresenter()

    private lateinit var username: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var newAccountLink: TextView
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        username = findViewById(R.id.login_username)
        phoneNumber = findViewById(R.id.login_phone_number)
        newAccountLink = findViewById(R.id.need_to_register)
        loginButton = findViewById(R.id.login_button)

        newAccountLink.setOnClickListener {
            val registerIntent = Intent(this, RegisterActivity::class.java)
            startActivity(registerIntent)
        }

        loginButton.setOnClickListener{
            val number = phoneNumber.text.toString().trim()
            if (number.isEmpty() || number.length < 10) {
                Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            } else {
                val fullNumber = "+1$number"
                val authIntent = Intent(this, PhoneAuthActivity::class.java)
                authIntent.putExtra("phonenumber", fullNumber)
                startActivity(authIntent)
            }
        }
    }

    override fun loginSuccessful() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun loginFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerFail() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerSuccessful() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

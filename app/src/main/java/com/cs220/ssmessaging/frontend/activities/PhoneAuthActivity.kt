package com.cs220.ssmessaging.frontend.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.cs220.ssmessaging.MyApplication.MyApplication
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.clientBackend.User
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : AppCompatActivity() {

    private lateinit var homeToolbar: Toolbar
    private lateinit var phonenumber: String
    private var verificationId: String = ""
    private lateinit var signinButton: Button
    private lateinit var authCode: EditText
    private lateinit var auth: FirebaseAuth

    private var isNewUser: Boolean = false
    private var firstname: String? = null
    private var lastname: String? = null
    private var username: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)

        homeToolbar = findViewById(R.id.home_toolbar)
        setSupportActionBar(homeToolbar)

        phonenumber = intent.getStringExtra("phonenumber")
        sendPhoneVerification(phonenumber)

        auth = FirebaseAuth.getInstance()

        firstname = intent.getStringExtra("firstname")
        lastname = intent.getStringExtra("lastname")
        username = intent.getStringExtra("username")
        isNewUser = intent.getBooleanExtra("newuser", false)

        authCode = findViewById(R.id.signin_code)
        signinButton = findViewById(R.id.signin_button)
        signinButton.setOnClickListener {
            val code = authCode.text.toString().trim()

            if (code.isEmpty() || code.length < 6) {
                authCode.error = "Invalid code"
                authCode.requestFocus()
                return@setOnClickListener
            }
            verifyCode(code)
        }


    }

    private fun setCurrentUser() {
        if (firstname != null && lastname != null && username != null) {
            val newUser = User(username.toString(), firstname.toString(), lastname.toString())
            MyApplication.currentUser = newUser
        }
    }

    private fun verifyCode(code: String) {
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            signInWithCredential(credential)
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "unable to log in due to connectivity issues, please try again",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val homeIntent = Intent(this, HomeActivity::class.java)
                    setCurrentUser()
                    if (isNewUser) {
                        MyApplication.currentUser!!.addSelfToDatabase()
                    }
                    startActivity(homeIntent)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "The code you have entered is incorrect. Please make sure to enter the code you received.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
    }

    private fun sendPhoneVerification(phonenumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phonenumber,
            60,
            TimeUnit.SECONDS,
            this,
            callbacks
        )
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onCodeSent(
            verification: String,
            forceResendingToken: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(verification, forceResendingToken)
            verificationId = verification
        }

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }
    }


}
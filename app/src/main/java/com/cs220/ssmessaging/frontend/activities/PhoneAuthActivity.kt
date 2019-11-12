package com.cs220.ssmessaging.frontend.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cs220.ssmessaging.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : AppCompatActivity() {

    private lateinit var phonenumber: String
    private lateinit var verificationId: String
    private lateinit var signinButton: Button
    private lateinit var authCode: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_auth)

        phonenumber = intent.getStringExtra("phonenumber")
        sendPhoneVerification(phonenumber)

        signinButton = findViewById(R.id.signin_button)
        authCode = findViewById(R.id.signin_code)
        signinButton.setOnClickListener {
            val code = authCode.text.toString().trim()

            if (code.isEmpty() || code.length < 6) {
                authCode.error = "Enter code..."
                authCode.requestFocus()
                return@setOnClickListener
            }
            verifyCode(code)
        }
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val homeIntent = Intent(this, HomeActivity::class.java)
                    startActivity(homeIntent)

                } else {
                    Toast.makeText(applicationContext, task.exception!!.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun sendPhoneVerification(phonenumber: String) {
        print("sending")
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
            s: String,
            forceResendingToken: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(s, forceResendingToken)
            verificationId = s
        }

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            val code = credential.smsCode
            if (code != null) {
                verifyCode(code)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }
    }
}
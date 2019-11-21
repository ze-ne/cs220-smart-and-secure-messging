package com.cs220.ssmessaging.frontend.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cs220.ssmessaging.R
import kotlinx.android.synthetic.main.activity_main.*

const val TAG = "MainActivity"

// NOTE: Not part of main implementation, currently in place for testing purposes only
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //        FirebaseFirestore.getInstance().collection("users")
        //            .addSnapshotListener { value, e ->
        //                if (e != null) {
        //                    Log.w(TAG, "Listen failed.", e)
        //                    return@addSnapshotListener
        //                }
        //
        //                for (doc in value!!) {
        //                    Log.d(TAG, doc.id)
        //                }
        //            }

        button.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        button4.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        button5.setOnClickListener {
            val intent = Intent(this, ConversationActivity::class.java)
            startActivity(intent)
        }

        button9.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}


package com.cs220.ssmessaging.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User as FBUser
import com.cs220.ssmessaging.clientBackend.User
import java.security.PublicKey

class FirebaseService {

    val db = FirebaseFirestore.getInstance()

/*
    fun getUserByCanonicalId(canonicalId: String): User? {
        db.collection("users")
            .whereEqualTo("canonicalId", canonicalId)
            .get()
            .addOnSuccessListener {  }
    }
*/
/*
    // getUserByDatabaseId

    // createUser
    fun createUser(newUser: User, password_hash : String, phone : String, publicKey: String): Boolean {
        var exists = false

        val usersRef = db.collection("users").document(newUser.userId)
        usersRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                } else {
                    exists = true
                }
            }
            .addOnFailureListener {
                exists = false
            }

        if (exists.not()) {
            return false
        }

        val toAdd = hashMapOf(
            "name" to newUser.userId,
            "canonicalId" to newUser.userId,
            "first_name" to newUser.firstName,
            "last_name" to newUser.lastName,
            "password_hash" to password_hash,
            "phone" to phone,
            "publicKey" to publicKey
        )

        var success = true
        //success@

        db.collection("users").document(newUser.userId)
            .set(toAdd)
            .addOnSuccessListener { }
            .addOnFailureListener {
                success = false
            }

        return success
    }

    // editUser
    fun editUser(newUser: User, password_hash : String, phone : String, publicKey: String): Boolean {
        val toAdd = hashMapOf(
            "name" to newUser.userId,
            "canonicalId" to newUser.userId,
            "first_name" to newUser.firstName,
            "last_name" to newUser.lastName,
            "password_hash" to password_hash,
            "phone" to phone,
            "publicKey" to publicKey
        )

        var success = true
        //success@

        db.collection("users").document(newUser.userId)
            .set(toAdd)
            .addOnSuccessListener { }
            .addOnFailureListener {
                success = false
            }

        return success
    }
*/
    // getPublicKey(user...)

    // authentication?

    // addConversation

    // sendMessage() - need to incorporate Google Store buckets for images

    // help frontned for snapshotListeners

}
/*
val testService = FirebaseService()

val usr = User("g1", "gray", "mackall")

val succ = testService.createUser(usr, "pw", "805", "ky")
*/
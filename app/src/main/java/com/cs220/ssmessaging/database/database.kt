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

    // getUserByDatabaseId

    private fun alreadyExists(collection : String, name : String) : Boolean {
        val ref = db.collection(collection).document(name)
        var exists = false

        ref.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    exists = true
                }
            }
            .addOnFailureListener {
                exists = false
            }

        return exists
    }

    // createUser
    fun createUser(newUser: User, password_hash : String, phone : String, publicKey: String): Boolean {

        if (alreadyExists("users", newUser.userId)) {
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
    fun editUser(user: User, password_hash : String, phone : String, publicKey: String): Boolean {

        if(alreadyExists("users", user.userId).not()) {
            return false
        }

        val toAdd = hashMapOf(
            "name" to user.userId,
            "canonicalId" to user.userId,
            "first_name" to user.firstName,
            "last_name" to user.lastName,
            "password_hash" to password_hash,
            "phone" to phone,
            "publicKey" to publicKey
        )

        var success = true
        //success@

        db.collection("users").document(user.userId)
            .set(toAdd)
            .addOnSuccessListener { }
            .addOnFailureListener {
                success = false
            }

        return success
    }
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
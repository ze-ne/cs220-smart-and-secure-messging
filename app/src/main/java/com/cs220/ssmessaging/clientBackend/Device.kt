package com.cs220.ssmessaging.clientBackend

import java.security.PrivateKey
import java.security.PublicKey

class Device(){
    // Variables that are non nullable MUST be declared at initialization time in Kotlin.
    /* Note, all accesses (except for the constructor) in Kotlin must go through the getter and setter.
     * This is because variables are properties and all properties have a private field.
     * Therefore we achieve encapsulation Kotlin by defining custom getters and setters.
     * That is why in most cases we don't need to declare private variables.
     */
    var pathToPrivateKey : String
        get() {
            // TODO
            return ""
        }
        set(privateKeyInput : String) {
            // TODO
        }

    var pathToPublicKey : String
        get() {
            // TODO
            return ""
        }
        set(publicKeyInput : String) {
            // TODO
        }

    constructor(privateKeyPathInput : String, publicKeyPathInput : String) : this(){
        // TODO
    }

    fun getPrivateKey() : PrivateKey? {
        // TODO
        return null
    }

    fun getPublicKey() : PublicKey? {
        // TODO
        return null
    }

    fun generateKeyPair() : Unit {
        // TODO
    }
}
package com.cs220.ssmessaging.clientBackend

import java.security.PrivateKey
import java.security.PublicKey
import java.security.KeyPairGenerator
import java.security.KeyPair

class Device(){
    /* For TAs: all accesses (except for the constructor) in Kotlin must go through the getter and setter.
     * This is because variables are properties and all properties have a private field.
     * Therefore we achieve encapsulation Kotlin by defining custom getters and setters.
     * This style is standard Kotlin convention called "Backing Fields".
     * That is why in some cases we don't need to declare variables with private modifiers.
     * In the cases that we do need private modifiers, it is when we need "Backing Properties" OR
     * when we want the variable getter and setter to be private.
     * Please go here for more information: https://kotlinlang.org/docs/reference/properties.html
     */
    var cipher : CipherExtension
        get(){
            // TODO
            return CipherExtension()
        }
        private set(cphr : CipherExtension){
            // TODO
        }

    var pathToPrivateKey : String
        get() {
            // TODO
            return "TODO"
        }
        set(privateKeyInput : String) {
            // TODO
        }

    var pathToPublicKey : String
        get() {
            // TODO
            return "TODO"
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

    fun generateKeyPair(pathToDir : String) : Unit {
        // TODO
    }
}
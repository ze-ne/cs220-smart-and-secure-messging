package com.cs220.ssmessaging.clientBackend

import java.security.*
import javax.crypto.CipherSpi

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

    /* The device constructor will try to find the keys in the default path.
     * If the keys are not in the path, then the constructor generates a default keypair,
     * stores the keypair in in the path, and also passes the keys into the CipherExtension.
     * Note that by default in Kotlin there is a getter and a setter automatically for cipher.
     */
    lateinit var cipher : CipherExtension
        private set

    // The paths to the keys should always be set to app/res/keys/myKey.<type>key
    val pathToMyPrivateKey : String
        get() {
            // TODO
            return "TODO"
        }

    val pathToMyPublicKey : String
        get() {
            // TODO
            return "TODO"
        }

    // If this method is called, a new key pair is generated and passed into cipher
    // Moreover, the new key pair is stored in the resources directory
    fun generateNewKeyPair() : Unit {
        // TODO
    }
}
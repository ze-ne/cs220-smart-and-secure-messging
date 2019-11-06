package com.cs220.ssmessaging.clientBackend
import com.cs220.ssmessaging.clientBackend.Message
import java.security.PrivateKey

import java.security.Provider
import java.security.PublicKey
import javax.crypto.Cipher
import javax.crypto.CipherSpi

class CipherExtension(privateKey: PrivateKey, publicKeys : MutableList<PublicKey>) {

    var publicKeyRing : MutableList<PublicKey>
        get(){
            // TODO
            return mutableListOf()
        }
        set(keys : MutableList<PublicKey>){
            // TODO
        }

    // Device should init this member with private key
    val decryptorCipher : Cipher
        get(){
            // TODO - placeholder transformation for skeleton code
            return Cipher.getInstance("AES")
        }

    // This should be initialized with the proper public key determined
    // in the encryptUnencryptedMessage function and selected from the keyring
    val encryptorCipher : Cipher
        get(){
            // TODO - placeholder transformation for skeleton code
            return Cipher.getInstance("AES")
        }

    fun decryptEncryptedMessage(encryptedMsg : EncryptedMessage) : UnencryptedMessage? {
        // TODO
        return null
    }

    fun encryptUnencryptedMessage(unencryptedMsg: UnencryptedMessage) : EncryptedMessage? {
        // TODO
        return null
    }
}
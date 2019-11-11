package com.cs220.ssmessaging.clientBackend
import com.cs220.ssmessaging.clientBackend.Message
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.w3c.dom.Text
import java.security.*

import javax.crypto.Cipher
import javax.crypto.CipherSpi

class CipherExtension(privateKey: PrivateKey, publicKeys : MutableMap<String, PublicKey>) {

    init{
        // Need to add bouncy castle provider
        Security.addProvider(BouncyCastleProvider())
    }
    // The key is the userId and the value is the public key
    var publicKeyRing : MutableMap<String, PublicKey> = publicKeys
        get(){
            return field
        }
        set(keysMap : MutableMap<String, PublicKey>){
            field = keysMap
        }

    var privateKey : PrivateKey = privateKey
        get(){
            return field
        }
        set(privtKey : PrivateKey){
            field = privtKey
        }

    // Device should init this member with private key
    val decryptorCipher : Cipher
        get(){
            return field
        }

    // This should be initialized with the proper public key determined
    // in the encryptUnencryptedMessage function and selected from the keyring
    val encryptorCipher : Cipher
        get(){
            return field
        }

    init{
        // Init ciphers
        decryptorCipher = Cipher.getInstance("RSA", "BC")
        decryptorCipher.init(Cipher.DECRYPT_MODE, privateKey)
        encryptorCipher = Cipher.getInstance("RSA", "BC")
    }

    fun addKeyToPublicKeyRing(userId : String, publicKey: PublicKey) : Unit {
        publicKeyRing.put(userId, publicKey)
    }

    fun decryptEncryptedMessage(encryptedMsg : EncryptedMessage) : UnencryptedMessage {
        if(encryptedMsg.messageType != "text" || encryptedMsg.messageType != "image"){
            throw Exception("Decrypting Message failed due to invalid type specified in encryptedMsg")
        }

        if(encryptedMsg.message.size == 0){
            throw Exception("Decrypting Message failed due to empty message body")
        }

        // TODO
        return TextMessage("", "", User(), User(), 1)
    }

    fun encryptUnencryptedMessage(unencryptedMsg: UnencryptedMessage) : EncryptedMessage {
        // TODO
        return EncryptedMessage(ByteArray(0), "", "", User(), User(), 1)
    }
}
package com.cs220.ssmessaging.clientBackend
import android.content.res.Resources
import android.media.Image
import com.cs220.ssmessaging.clientBackend.Message
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.w3c.dom.Text
import java.security.*

import javax.crypto.Cipher
import javax.crypto.CipherSpi

class CipherExtension(privateKey: PrivateKey, publicKeys : MutableMap<String, PublicKey>) {

    companion object{
        // Constant for charset
        val CHARSET = Charsets.UTF_8
    }

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
        if(!EncryptedMessage.isValidMessage(encryptedMsg)){
            throw InvalidParameterException("Decrypting Message failed due to invalid EncryptedMessage")
        }

        // Need to init decryptor cipher with new private key every time (this is how it works for Cipher)
        decryptorCipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes : ByteArray = decryptorCipher.doFinal(encryptedMsg.message)

        return when(encryptedMsg.messageType == "image") {
            true ->
                ImageMessage(decryptedBytes, encryptedMsg.conversationId, encryptedMsg.senderId, encryptedMsg.recipientId, encryptedMsg.timestamp)
            false ->
                TextMessage(decryptedBytes.toString(CHARSET), encryptedMsg.conversationId, encryptedMsg.senderId, encryptedMsg.recipientId, encryptedMsg.timestamp)
        }
    }

    fun encryptUnencryptedMessage(unencryptedMsg : UnencryptedMessage) : EncryptedMessage {
        // Short circuiting allows us to do such logical operations
        if((unencryptedMsg is ImageMessage && !ImageMessage.isValidMessage(unencryptedMsg)) ||
            (unencryptedMsg is TextMessage && !TextMessage.isValidMessage(unencryptedMsg))){
            throw InvalidParameterException("Encrypting Message failed due to invalid UnencryptedMessage")
        }

        val recipientIdPublicKey : PublicKey? = publicKeyRing[unencryptedMsg.recipientId]

        if(recipientIdPublicKey == null){
            throw NullPointerException("senderId public key not found. This means that the keys are unsynced with the server")
        }

        // Need to init encryptor cipher with new private key every time (this is how it works for Cipher)
        encryptorCipher.init(Cipher.ENCRYPT_MODE, recipientIdPublicKey)
        var encryptedByteArray : ByteArray
        var messageType : String

        if(unencryptedMsg is ImageMessage){
            encryptedByteArray = encryptorCipher.doFinal(unencryptedMsg.message)
            messageType = "image"
        }
        else{
            val textByteArray : ByteArray = (unencryptedMsg as TextMessage).message.toByteArray(CHARSET)
            encryptedByteArray = encryptorCipher.doFinal(textByteArray)
            messageType = "text"
        }

        return EncryptedMessage(encryptedByteArray, unencryptedMsg.conversationId, messageType, unencryptedMsg.senderId, unencryptedMsg.recipientId, unencryptedMsg.timestamp)
    }
}
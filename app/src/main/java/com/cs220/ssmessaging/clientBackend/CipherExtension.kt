package com.cs220.ssmessaging.clientBackend
import android.graphics.Bitmap
import android.util.Log
import java.lang.Exception
import java.security.*

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec
import org.bouncycastle.asn1.iana.IANAObjectIdentifiers.security
import java.io.ByteArrayOutputStream


class CipherExtension(privateKey: PrivateKey, publicKeys : MutableMap<String, PublicKey>) {

    companion object{
        // Constant for charset
        val CHARSET = Charsets.UTF_8
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
        decryptorCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        decryptorCipher.init(Cipher.DECRYPT_MODE, privateKey)
        encryptorCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    }

    fun addKeyToPublicKeyRing(userId : String, publicKey: PublicKey) : Unit {
        publicKeyRing.put(userId, publicKey)
    }

    fun decryptEncryptedMessage(encryptedMsg : EncryptedMessage) : UnencryptedMessage {
        if(!EncryptedMessage.isValidMessage(encryptedMsg)){
            throw InvalidParameterException("Decrypting Message failed due to invalid EncryptedMessage")
        }

        // Need to init decryptor cipher with new private key every time (this is how it works for Cipher)
        // Then decrypt the AES Key
        decryptorCipher.init(Cipher.DECRYPT_MODE, privateKey)
        var decryptedRecpientAESKeyBytes : ByteArray
        var decryptedSenderAESKeyBytes : ByteArray

        // Need these try catches since decryption could error out for the wrong key
        try{
            decryptedRecpientAESKeyBytes = decryptorCipher.doFinal(encryptedMsg.encryptedRecipientAESKey)
        }
        catch(e : Exception){
            decryptedRecpientAESKeyBytes = byteArrayOf()
        }

        decryptorCipher.init(Cipher.DECRYPT_MODE, privateKey)

        try{
            decryptedSenderAESKeyBytes = decryptorCipher.doFinal(encryptedMsg.encryptedSenderAESKey)
        }
        catch(e: Exception){
            decryptedSenderAESKeyBytes = byteArrayOf()
        }

        val trueDecryptedBytes =
            if(decryptedRecpientAESKeyBytes.size == 16)
                decryptedRecpientAESKeyBytes
            else
                decryptedSenderAESKeyBytes

        // Now we need to check if the trueDecryptedBytes is not equal to 16.
        // If not, then make the message a "corruption message"
        if(trueDecryptedBytes.size != 16){
            if(encryptedMsg.messageType == "image") {
                val bmp : Bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
                val byteStream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.PNG, 100, byteStream)
                val emptyBitmapBytes = byteStream.toByteArray()

                return ImageMessage(
                    emptyBitmapBytes,
                    encryptedMsg.conversationId,
                    encryptedMsg.senderId,
                    encryptedMsg.recipientId,
                    encryptedMsg.timestamp
                )
            }
            else {
                return TextMessage("Corruption: Keys have changed! " +
                            "This means that the app was reinstalled and the keys were overwritten. " +
                            "This user or the other user is now useless.",
                        encryptedMsg.conversationId, encryptedMsg.senderId, encryptedMsg.recipientId, encryptedMsg.timestamp)
            }
        }

        val aesKey = SecretKeySpec(trueDecryptedBytes, "AES")

        // Now create the AES Cipher with the key and decrypt the bytes of the message
        // Use ECB (default) padding for now. Might change to CBC later.
        val aesCipher = Cipher.getInstance("AES")
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey)
        val decryptedMessageBytes = aesCipher.doFinal(encryptedMsg.message)

        return when(encryptedMsg.messageType == "image") {
            true ->
                ImageMessage(decryptedMessageBytes, encryptedMsg.conversationId, encryptedMsg.senderId, encryptedMsg.recipientId, encryptedMsg.timestamp)
            false ->
                TextMessage(decryptedMessageBytes.toString(CHARSET), encryptedMsg.conversationId, encryptedMsg.senderId, encryptedMsg.recipientId, encryptedMsg.timestamp)
        }
    }

    fun encryptUnencryptedMessage(unencryptedMsg : UnencryptedMessage) : EncryptedMessage {
        // Short circuiting allows us to do such logical operations
        if((unencryptedMsg is ImageMessage && !ImageMessage.isValidMessage(unencryptedMsg)) ||
            (unencryptedMsg is TextMessage && !TextMessage.isValidMessage(unencryptedMsg))){
            throw InvalidParameterException("Encrypting Message failed due to invalid UnencryptedMessage")
        }

        val recipientIdPublicKey : PublicKey? = publicKeyRing[unencryptedMsg.recipientId]
        val myKey : PublicKey? = publicKeyRing["myKey"]

        if(recipientIdPublicKey == null || myKey  == null){
            throw NullPointerException("recipientId or my public key not found. This means that the keys are unsynced with the server")
        }

        // What we now have to do is create a random AES key for encryption and initialize an AES Cipher
        val aesKeyGen = KeyGenerator.getInstance("AES")
        aesKeyGen.init(128)
        val aesKey = aesKeyGen.generateKey()
        // Use ECB (default) padding for now. Might change to CBC later.
        val aesCipher = Cipher.getInstance("AES")
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey)

        var encryptedByteArray : ByteArray
        var messageType : String

        // Encrypt the message with AES, then encrypt the aes key with RSA
        if(unencryptedMsg is ImageMessage){
            encryptedByteArray = aesCipher.doFinal(unencryptedMsg.message)
            messageType = "image"
        }
        else{
            val textByteArray : ByteArray = (unencryptedMsg as TextMessage).message.toByteArray(CHARSET)
            encryptedByteArray = aesCipher.doFinal(textByteArray)
            messageType = "text"
        }

        // Need to init encryptor cipher with new public key every time (this is how it works for Cipher)
        encryptorCipher.init(Cipher.ENCRYPT_MODE, recipientIdPublicKey)
        val encryptedRecipientAESKeyBytes = encryptorCipher.doFinal(aesKey.encoded)

        encryptorCipher.init(Cipher.ENCRYPT_MODE, myKey)
        val encryptedSenderAESKeyBytes = encryptorCipher.doFinal(aesKey.encoded)

        return EncryptedMessage(encryptedByteArray, unencryptedMsg.conversationId,
            messageType, unencryptedMsg.senderId, unencryptedMsg.recipientId, unencryptedMsg.timestamp, encryptedSenderAESKeyBytes, encryptedRecipientAESKeyBytes)
    }
}
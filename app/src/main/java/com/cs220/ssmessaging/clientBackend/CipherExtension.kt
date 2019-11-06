package com.cs220.ssmessaging.clientBackend
import com.cs220.ssmessaging.clientBackend.Message

import java.security.Provider
import javax.crypto.Cipher
import javax.crypto.CipherSpi

class CipherExtension() {

    val javaCipher : Cipher
        get(){
            // TODO - placeholder transformation for skeleton code
            return Cipher.getInstance("AES")
        }

    fun decryptEncryptedMessage(encryptedMsg : EncryptedMessage) : UnencryptedMessage{
        // TODO
        return TextMessage("", "", User(), -1)
    }

    fun encryptEncryptedMessage(unencryptedMsg: UnencryptedMessage) : EncryptedMessage {
        // TODO
        return EncryptedMessage(ByteArray(0), "", User(), -1)
    }
}
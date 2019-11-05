package com.cs220.ssmessaging.clientBackend
import com.cs220.ssmessaging.clientBackend.Message

import java.security.Provider
import javax.crypto.Cipher
import javax.crypto.CipherSpi

class CipherExtension(cipherSpi : CipherSpi, provider : Provider, transformation : String) :
    Cipher(cipherSpi, provider, transformation){

    fun decryptEncryptedMessage(encryptedMsg : EncryptedMessage) : UnencryptedMessage{
        // TODO
        return TextMessage()
    }

    fun encryptEncryptedMessage(unencryptedMsg: UnencryptedMessage) : EncryptedMessage {
        // TODO
        return EncryptedMessage()
    }
}
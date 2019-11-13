package com.cs220.ssmessaging.clientBackend

import com.cs220.ssmessaging.MyApplication.MyApplication
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.file.Files
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.security.Security
import javax.crypto.Cipher
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

    // Companion object stores static constants
    companion object{
        private val keyDir : String = MyApplication.appContext?.getFilesDir()?.path.toString()
        private var publicKeyFileName : String = "myKey.publicKey"
        private var privateKeyFileName : String = "myKey.privateKey"
    }
    /* The device constructor will try to find the keys in the default path.
     * If the keys are not in the path, then the constructor generates a default keypair,
     * stores the keypair in in the path, and also passes the keys into the CipherExtension.
     * Note that by default in Kotlin there is a getter and a setter automatically for cipher.
     */
    lateinit var cipher : CipherExtension
        private set

    init{
        // Need to add bouncy castle provider
        Security.addProvider(BouncyCastleProvider())
    }

    // First init checks if the keys exist. If not, then generate the new keys.
    init {
        val publicKeyFile = File(keyDir + publicKeyFileName)
        val privateKeyFile = File(keyDir + privateKeyFileName)
        // If either one of the two files does not exist, generate a new key pair
        if (!publicKeyFile.exists() || !privateKeyFile.exists()) {
            publicKeyFile.createNewFile()
            privateKeyFile.createNewFile()

            val keyPairGen: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGen.initialize(2048)
            val keyPair: KeyPair = keyPairGen.generateKeyPair()

            val privateKey = keyPair.private
            val publicKey = keyPair.public
            val publicKeyBytes: ByteArray = publicKey.encoded
            val privateKeyBytes: ByteArray = privateKey.encoded

            val publicKeyStream: FileOutputStream = FileOutputStream(publicKeyFile)
            val privateKeyStream: FileOutputStream = FileOutputStream(privateKeyFile)

            publicKeyStream.write(publicKeyBytes)
            privateKeyStream.write(privateKeyBytes)
            publicKeyStream.close()
            privateKeyStream.close()
        }
    }

    // Second init initializes the cipher object
    init {
        // We now get the public and private keys and store them in the cipher extension
        val keyFactory : KeyFactory = KeyFactory.getInstance("RSA")


        val keyFiles : Array<File>? = File(keyDir).listFiles()

        if(keyFiles == null)
            throw FileNotFoundException("No key files found in key directory when initializing CipherExtension")

        var myPrivateKey : PrivateKey = keyFactory.generatePrivate(
            PKCS8EncodedKeySpec(Files.readAllBytes(File(keyDir + privateKeyFileName).toPath())))

        var publicKeyRing : MutableMap<String, PublicKey> = mutableMapOf()

        for(keyFile : File in keyFiles){
            val fileNameComponents : List<String> = keyFile.name.split(".")
            if(fileNameComponents.size == 2 && fileNameComponents[1] == "publicKey"){
                // Note: if the name of the public key file is
                // publicKeyName.publicKey, then the userId of the key owner is publicKeyName
                // The only exception is my own public key
                var publicKeyUserId : String = fileNameComponents[0]
                var publicKey : PublicKey =
                    keyFactory.generatePublic(X509EncodedKeySpec(Files.readAllBytes(keyFile.toPath())))
                publicKeyRing.put(publicKeyUserId, publicKey)
            }
        }

        cipher = CipherExtension(myPrivateKey, publicKeyRing)
    }

    // The paths to the keys should always be set to app/res/keys/myKey.<type>key
    // The type of data in the key is ByteArray and is stored as a string
    val pathToMyPrivateKey : String = keyDir + privateKeyFileName
        get() {
            return field
        }

    val pathToMyPublicKey : String = keyDir + publicKeyFileName
        get() {
            return field
        }

    // If this method is called, a new key pair is generated and passed into cipher
    // Moreover, the new key pair is stored in the resources directory
    // Probably needs refactoring later on.
    fun generateNewKeyPair() : Unit {
        val keyPairGen : KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGen.initialize(2048)
        val keyPair : KeyPair = keyPairGen.generateKeyPair()

        val privateKey = keyPair.private
        val publicKey = keyPair.public
        val publicKeyBytes : ByteArray = publicKey.encoded
        val privateKeyBytes : ByteArray = privateKey.encoded

        val publicKeyStream : FileOutputStream = FileOutputStream(keyDir + publicKeyFileName)
        val privateKeyStream : FileOutputStream = FileOutputStream(keyDir + privateKeyFileName)

        publicKeyStream.write(publicKeyBytes)
        privateKeyStream.write(privateKeyBytes)

        publicKeyStream.close()
        privateKeyStream.close()

        cipher.publicKeyRing.put("myKey", publicKey)
        cipher.privateKey = privateKey
    }

    // add another user's public key to both local storage and key ring (temporary)
    fun addUserPublicKey(userId: String, publicKey: PublicKey): Boolean {
        var publicKeyFile = File("$keyDir$userId.publicKey")

        // If either one of the two files does not exist, generate a new key pair
        if (!publicKeyFile.exists()) {
            publicKeyFile.createNewFile()
            val publicKeyStream = FileOutputStream(publicKeyFile)
            publicKeyStream.write(publicKey.encoded)
            publicKeyStream.close()

            cipher.addKeyToPublicKeyRing(userId, publicKey)

            return true
        }

        return false
    }
}
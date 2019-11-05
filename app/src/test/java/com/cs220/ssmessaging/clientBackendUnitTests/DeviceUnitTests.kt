package com.cs220.ssmessaging.clientBackendUnitTests

import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

import com.cs220.ssmessaging.clientBackend.Device

@RunWith(MockitoJUnitRunner::class)
class DeviceUnitTests{
    @Test
    fun testDefaultConstructor() {
        val testDevice = Device()
        assertEquals("", testDevice.pathToPublicKey)
        assertEquals("", testDevice.pathToPrivateKey)
    }

    @Test
    fun testSecondaryConstructorValidRelativePath(){
        // Test valid constructor args
        var testDevice = Device("abc", "def")
        assertEquals("abc", testDevice.pathToPublicKey)
        assertEquals("def", testDevice.pathToPrivateKey)

        testDevice = Device("abc/relative.png", "../test_relative")
        assertEquals("abc/relative.png", testDevice.pathToPublicKey)
        assertEquals("../test_relative", testDevice.pathToPrivateKey)

        testDevice = Device("C:/absolute/test.png", "C:/absolute/test")
        assertEquals("C:/absolute/test.png", testDevice.pathToPublicKey)
        assertEquals("C:/absolute/test", testDevice.pathToPrivateKey)

        testDevice = Device("C:/absolute/test.png", ".")
        assertEquals("C:/absolute/test.png", testDevice.pathToPublicKey)
        assertEquals(".", testDevice.pathToPrivateKey)
    }

    @Test
    fun testSecondaryConstructorInvalidPath(){
        // Test invalid constructor args
        var testDevice = Device("C:/absolute/test.png<><>", "****")
        assertEquals("", testDevice.pathToPublicKey)
        assertEquals("", testDevice.pathToPrivateKey)

        testDevice = Device("", "")
        assertEquals("", testDevice.pathToPublicKey)
        assertEquals("", testDevice.pathToPrivateKey)

        testDevice = Device("!@$", "*^@")
        assertEquals("", testDevice.pathToPublicKey)
        assertEquals("", testDevice.pathToPrivateKey)
    }

    @Test
    fun testSecondaryConstructorOnePathValidOtherInvalid(){
        // Test one argument valid one argument invalid
        var testDevice = Device("C:/absolute/test.png", "****")
        assertEquals("C:/absolute/test.png", testDevice.pathToPublicKey)
        assertEquals("", testDevice.pathToPrivateKey)

        testDevice = Device("@@<>@", "C:/absolute/test")
        assertEquals("", testDevice.pathToPublicKey)
        assertEquals("C:/absolute/test", testDevice.pathToPrivateKey)
    }

    @Test
    fun testPathToPublicKeyValid() {
        // Test the getter and setter for valid private key
        var testDevice = Device()
        testDevice.pathToPublicKey = "C:/absolute/test.png"
        assertEquals("C:/absolute/test", testDevice.pathToPublicKey)

        testDevice.pathToPublicKey = "."
        assertEquals(".", testDevice.pathToPublicKey)

        testDevice.pathToPublicKey = "relative.png"
        assertEquals("relative.png", testDevice.pathToPublicKey)

        testDevice.pathToPublicKey = "../dotdotrelative.txt"
        assertEquals("../dotdotrelative.txt", testDevice.pathToPublicKey)
    }

    @Test
    fun testPathToPublicKeyInValid() {
        // Test the getter and setter for invalid private key
        var testDevice = Device()
        testDevice.pathToPublicKey = "false"

        testDevice.pathToPublicKey = "**"
        assertEquals("false", testDevice.pathToPublicKey)

        testDevice.pathToPublicKey = ""
        assertEquals("false", testDevice.pathToPublicKey)

        testDevice.pathToPublicKey = "!<>@"
        assertEquals("false", testDevice.pathToPublicKey)
    }

    @Test
    fun testPathToPrivateKeyValid() {
        // Test the getter and setter for valid private key
        var testDevice = Device()
        testDevice.pathToPrivateKey = "C:/absolute/test.png"
        assertEquals("C:/absolute/test", testDevice.pathToPrivateKey)

        testDevice.pathToPrivateKey = "."
        assertEquals(".", testDevice.pathToPrivateKey)

        testDevice.pathToPrivateKey = "relative.png"
        assertEquals("relative.png", testDevice.pathToPrivateKey)

        testDevice.pathToPrivateKey = "../dotdotrelative.txt"
        assertEquals("../dotdotrelative.txt", testDevice.pathToPrivateKey)
    }

    @Test
    fun testPathToPrivateKeyInValid() {
        // Test the getter and setter for invalid private key
        var testDevice = Device()
        testDevice.pathToPrivateKey = "false"

        testDevice.pathToPrivateKey = "**"
        assertEquals("false", testDevice.pathToPrivateKey)

        testDevice.pathToPrivateKey = ""
        assertEquals("false", testDevice.pathToPrivateKey)

        testDevice.pathToPrivateKey = "!<>@"
        assertEquals("false", testDevice.pathToPrivateKey)
    }

}
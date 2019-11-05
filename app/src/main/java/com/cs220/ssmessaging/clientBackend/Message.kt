package com.cs220.ssmessaging.clientBackend

interface Message {
}

class EncryptedMessage : Message {
}

interface UnencryptedMessage : Message {
}

class TextMessage : UnencryptedMessage {
}

class ImageMessage : UnencryptedMessage {
}
package com.cs220.ssmessaging.database

import com.cs220.ssmessaging.clientBackend.EncryptedMessage


data class MessageData(
    val bucket_url: String = "",
    val data: String = "",
    val message_type: Int = 0,
    val sender_id: String = "",
    val time_sent: Int = 0
    )
/*
data class MessageData {
    constructor(msg : EncryptedMessage) : this() {
        this.bucketUrl = ""
        this.data =
    }

    private var bucketUrl : String = ""
    get() {
        return field
    }
    set(bucket_url : String) {
        field = bucket_url
    }
}
*/


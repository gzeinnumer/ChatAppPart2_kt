package com.gzeinnumer.chatapppart2_kt.model

data class Chat(
    val sender: String? = null,
    val receiver: String? = null,
    val message: String? = null,
    val isseen: Boolean? = null
)

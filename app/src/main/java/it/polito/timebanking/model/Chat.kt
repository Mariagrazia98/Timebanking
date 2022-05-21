package it.polito.timebanking.model

import java.io.Serializable

class Chat (
    var receiverUid: String = "",
    var messageList: MutableList<ChatMessage> = mutableListOf()
) : Serializable {}
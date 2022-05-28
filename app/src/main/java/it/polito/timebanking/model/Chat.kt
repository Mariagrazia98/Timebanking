package it.polito.timebanking.model

import java.io.Serializable

class Chat (
    var id: String = "",
    var receiverUid: String = "",
    var chatStatus:Int=0 //0 = open chat, 1 closed chat
) : Serializable {}
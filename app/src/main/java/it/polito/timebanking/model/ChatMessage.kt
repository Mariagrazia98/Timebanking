package it.polito.timebanking.model

import java.io.Serializable

class ChatMessage (
    var id:String = "",
    var text:String = "",
    var type: Int = 1, //sent=0 received=1
    var date:String = "",
    var time:String = ""
) : Serializable {}
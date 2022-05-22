package it.polito.timebanking.model

import java.io.Serializable

class Chat (
    var id: String = "",
    var receiverUid: String = "",
) : Serializable {}
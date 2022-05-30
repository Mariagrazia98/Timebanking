package it.polito.timebanking.model

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.LocalDateTime
import java.util.*

class Review (
    var id:String = "",
    var userIdReviewer: String = "",
    var nameReviewer:String = "",
    var timeSlotId: String = "",
    var comment:String = "",
    var type: Int = -1, //0: offerer, 1: receiver
    var date:String = "",
    var rating : Float = 0f
) : Serializable {}
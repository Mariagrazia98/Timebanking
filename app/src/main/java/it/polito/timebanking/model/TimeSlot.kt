package it.polito.timebanking.model

import java.io.Serializable

class TimeSlot(
    var id:String = "id",
    var title:String = "",
    var description:String? = "",
    var date:String = "",
    var time:String = "",
    var duration:Int = 0,
    var location:String = "",
    var skills:MutableList<String> = mutableListOf(),
    var status:Int=0, //0 = available , 1 = assigned
    var reviewState: Int=0, //0= no review, 1=review from offer, 2=review from receiver, 3=review from both
    var idReceiver:String?=null,
    ) : Serializable {}
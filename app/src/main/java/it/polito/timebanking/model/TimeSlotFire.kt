package it.polito.timebanking.model

import java.io.Serializable

class TimeSlotFire(
    var id:String = "id",
    var title:String = "",
    var description:String? = "",
    var date:String = "",
    var time:String = "",
    var duration:Int = 0,
    var location:String = "",
    var skills:MutableList<String> = mutableListOf()
    ) : Serializable {
}
package it.polito.timebanking.model

import java.io.Serializable

class User(var uid: String = "uid", var fullname: String = "", var nickname: String = "",
           var email: String = "email@address.com",
           var location: String = "",
           var description:String="",
           var skills:MutableList<String> = mutableListOf(),
           var age:Int=18,
           var imagePath:String? = "") : Serializable {
}
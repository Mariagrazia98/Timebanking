package it.polito.timebanking.repository

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index("fullname","nickname","email","location")])
class User {
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
    //Lab2
    var fullname:String = ""
    var nickname:String = ""
    var email:String = ""
    var location:String = ""
    var description:String=""
    var skills:String = ""
    var age:Int=18;
    //Lab3
    //var slots:List<Int>???
}
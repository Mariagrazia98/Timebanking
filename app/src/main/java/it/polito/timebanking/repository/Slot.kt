package it.polito.timebanking.repository

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "slots", indices = [Index("title","description","date","time","duration","location")])
class Slot {
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
    //Lab3
    var title:String = ""
    var description:String? = ""
    var date:String = ""
    var time:String = ""
    var duration:Int = 0
    var location:String = ""
}
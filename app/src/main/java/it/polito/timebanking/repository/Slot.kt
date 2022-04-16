package it.polito.timebanking.repository

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "slots", indices = [Index("")])
class Slot {
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
    
}
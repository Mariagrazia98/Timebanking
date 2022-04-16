package it.polito.timebanking.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Slot::class],version = 1)
abstract class SlotDatabase: RoomDatabase() {
    abstract fun slotDao(): SlotDao

    companion object{
        @Volatile
        private var INSTANCE: SlotDatabase? = null

        fun getDatabase(context: Context): SlotDatabase? =
            (
                    INSTANCE?:
                    synchronized(this) {
                        val i = INSTANCE ?: Room.databaseBuilder(
                            context.applicationContext,
                            SlotDatabase::class.java,
                            "slots"
                        ).build()
                        INSTANCE = i
                        INSTANCE
                    }
                    )
    }
}
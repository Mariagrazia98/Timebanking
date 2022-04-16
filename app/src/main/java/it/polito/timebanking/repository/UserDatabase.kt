package it.polito.timebanking.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Slot::class],version = 1)
abstract class UserDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object{
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase? =
            (
                    INSTANCE?:
                    synchronized(this) {
                        val i = INSTANCE ?: Room.databaseBuilder(
                            context.applicationContext,
                            UserDatabase::class.java,
                            "users"
                        ).build()
                        INSTANCE = i
                        INSTANCE
                    }
            )
    }
}
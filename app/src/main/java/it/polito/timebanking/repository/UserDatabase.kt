package it.polito.timebanking.repository

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [User::class],
    version = 2
)
abstract class UserDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object{
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase? =
            (
                    INSTANCE?:
                    synchronized(this) {
                        val MIGRATION_1_2 = object : Migration(1,2){
                            override fun migrate(database: SupportSQLiteDatabase) {
                                database.execSQL("ALTER TABLE 'users' ADD COLUMN 'imagePath' TEXT")
                            }
                        }
                        val i = INSTANCE ?: Room.databaseBuilder(
                            context.applicationContext,
                            UserDatabase::class.java,
                            "users"
                        ).addMigrations(MIGRATION_1_2).build()
                        INSTANCE = i
                        INSTANCE
                    }


            )
    }
}
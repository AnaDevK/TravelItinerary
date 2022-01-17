package com.app.mapyourway.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.mapyourway.models.Place
import com.app.mapyourway.models.UserMap


@Database(entities = [UserMap::class, Place::class], version = 1, exportSchema = false)
abstract class UserMapsDatabase : RoomDatabase() {

    abstract fun userMapsDatabaseDao(): UserMapsDao

//    companion object {
//
//        @Volatile
//        private var INSTANCE: UserMapsDatabase? = null
//
//        fun getDatabase(context: Context): UserMapsDatabase {
//            synchronized(this) {
//                var instance = INSTANCE
//
//                if (instance == null) {
//                    instance = Room.databaseBuilder(
//                        context.applicationContext,
//                        UserMapsDatabase::class.java,
//                        "usermaps_history_database"
//                    )
//                        .fallbackToDestructiveMigration()
//                        .build()
//                    INSTANCE = instance
//                }
//                return instance
//            }
//        }
//    }

    companion object {
        @Volatile
        private var INSTANCE: RoomDatabase? = null

        fun getDatabase(context: Context): UserMapsDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance as UserMapsDatabase
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserMapsDatabase::class.java,
                    "datarecords_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
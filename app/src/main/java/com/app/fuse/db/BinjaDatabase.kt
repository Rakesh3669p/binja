package com.app.fuse.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.fuse.ui.mainScreen.home.FriendsList.model.FriendDetails


@Database(
    entities = [FriendDetails::class],
    version = 8
)


abstract class BinjaDatabase : RoomDatabase() {
    abstract fun getBinjaDao(): BinjaDao
    companion object {
        @Volatile
        private var INSTANCE: BinjaDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = INSTANCE ?: synchronized(LOCK) {

            INSTANCE ?: createDataBase(context).also { INSTANCE = it }
        }

        private fun createDataBase(context: Context) =
            Room.databaseBuilder(context.applicationContext, BinjaDatabase::class.java, "binja_db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build().also {
                    INSTANCE = it
                }
    }
}

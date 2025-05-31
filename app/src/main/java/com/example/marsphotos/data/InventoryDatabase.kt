package com.example.marsphotos.data

import android.content.Context
import android.content.pm.InstallSourceInfo
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Pref::class], version = 1, exportSchema = false)
abstract class InventoryDatabase: RoomDatabase(){

    abstract fun prefDao(): PrefDao

    companion object {
        @Volatile
        private var Instance: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase {

            return Instance ?: synchronized(this){
                Room.databaseBuilder(context, InventoryDatabase::class.java, "pref_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }

            }
        }
    }
}


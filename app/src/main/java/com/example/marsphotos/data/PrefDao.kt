package com.example.marsphotos.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

import kotlinx.coroutines.flow.Flow

@Dao
interface PrefDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(pref: Pref)

    @Update
    suspend fun update(pref: Pref)

    @Delete
    suspend fun delete(pref: Pref)

    @Query("SELECT * from preferences WHERE id = :id")
    fun getItem(id: Int): Flow<Pref>

    @Query("SELECT * from preferences ORDER BY keyword ASC")
    fun getAllItems(): Flow<List<Pref>>
}


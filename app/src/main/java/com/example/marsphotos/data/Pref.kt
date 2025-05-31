package com.example.marsphotos.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preferences")
data class Pref (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val keyword: String
)
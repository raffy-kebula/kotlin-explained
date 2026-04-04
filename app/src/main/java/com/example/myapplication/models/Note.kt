package com.example.myapplication.models

import androidx.room.Entity
import androidx.room.PrimaryKey

// 2. Entity
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String
)
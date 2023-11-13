package com.example.videocallapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_logs")
data class CallLogs(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val phoneNumber: String,
    val callDuration: String,
    val timeStamp: String
)
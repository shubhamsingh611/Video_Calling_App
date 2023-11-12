package com.example.videocallapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.videocallapp.dao.CallLogsDao
import com.example.videocallapp.model.CallLogs

@Database(entities = [CallLogs::class], version = 1)
abstract class CallLogsDatabase : RoomDatabase() {
    abstract fun callLogsDao(): CallLogsDao
    companion object {
        private var INSTANCE: CallLogsDatabase? = null
        fun getDatabase(context: Context): CallLogsDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        CallLogsDatabase::class.java,
                        "call_logs_database"
                    )
                        .createFromAsset("call_logs.db")
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}
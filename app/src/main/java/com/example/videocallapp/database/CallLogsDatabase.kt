package com.example.videocallapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.videocallapp.dao.CallLogsDao
import com.example.videocallapp.model.CallLogs
import com.example.videocallapp.utils.AppConstants

@Database(entities = [CallLogs::class], version = 1)
abstract class CallLogsDatabase : RoomDatabase() {
    abstract fun callLogsDao(): CallLogsDao
    companion object {
        @Volatile
        private var INSTANCE: CallLogsDatabase? = null
        fun getDatabase(context: Context): CallLogsDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        CallLogsDatabase::class.java,
                        AppConstants.LOGS_DATABASE_NAME
                    )
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}
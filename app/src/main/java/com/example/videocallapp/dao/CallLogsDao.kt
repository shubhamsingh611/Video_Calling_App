package com.example.videocallapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.videocallapp.model.CallLogs;

@Dao
interface CallLogsDao {
    //This method will be implemented in background thread
    // As it is returning live Data
    @Query("SELECT * from callLogs")
    fun getCallLogs() :LiveData<MutableList<CallLogs>>
    @Insert
    fun insertCallLogs(callLogs: CallLogs)
}
package com.example.videocallapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.videocallapp.model.CallLogs;

@Dao
interface CallLogsDao {
    @Query("SELECT * from call_logs")
    fun getCallLogs() : LiveData<List<CallLogs>>

    //Adding Records to Table
    @Insert
    fun insertCallLogs(callLogs: CallLogs)
}
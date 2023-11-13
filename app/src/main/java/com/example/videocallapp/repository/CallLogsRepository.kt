package com.example.videocallapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.videocallapp.dao.CallLogsDao
import com.example.videocallapp.database.CallLogsDatabase
import com.example.videocallapp.model.CallLogs

class CallLogsRepository(private val callLogsDao : CallLogsDao) {
    fun getCallLogs() : LiveData<List<CallLogs>> {
        return callLogsDao.getCallLogs()
    }
    //Inserting data in database
    suspend fun insertCallLogs(callLogs: CallLogs){
        callLogsDao.insertCallLogs(callLogs)
    }
}
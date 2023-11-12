package com.example.videocallapp.repository

import androidx.lifecycle.LiveData
import com.example.videocallapp.dao.CallLogsDao
import com.example.videocallapp.model.CallLogs

class CallLogsRepository(private val callLogsDao : CallLogsDao) {
    fun getCallLogs() : LiveData<MutableList<CallLogs>> {
        return callLogsDao.getCallLogs()
    }
    suspend fun insertCallLogs(callLogs: CallLogs){
        callLogsDao.insertCallLogs(callLogs)
    }
}
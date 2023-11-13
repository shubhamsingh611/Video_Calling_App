package com.example.videocallapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videocallapp.model.CallLogs
import com.example.videocallapp.repository.CallLogsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CallLogsFragmentViewModel(private val callLogsRepository: CallLogsRepository) : ViewModel() {
    //Live Data for observing change in Call Logs
    fun getCallLogs() : LiveData<List<CallLogs>> {
        return callLogsRepository.getCallLogs()
    }
    fun insertCallLogs(callLogs: CallLogs){
        //onBackground thread
        viewModelScope.launch(Dispatchers.IO) {
            callLogsRepository.insertCallLogs(callLogs)
        }
    }
}
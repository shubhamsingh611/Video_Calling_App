package com.example.videocallapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videocallapp.model.CallLogs
import com.example.videocallapp.repository.CallLogsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// All data related to Views we maintain in view model
//View Model talks with Repo and Repo talks with external source like DB or remote API
class CallLogsFragmentViewModel(private val callLogsRepository: CallLogsRepository) : ViewModel() {
    fun getCallLogs() : LiveData<MutableList<CallLogs>> {
        return callLogsRepository.getCallLogs()
    }
    fun insertCallLogs(callLogs: CallLogs){
        //onBackground thread
        viewModelScope.launch(Dispatchers.IO) {
            callLogsRepository.insertCallLogs(callLogs)
        }
    }
}
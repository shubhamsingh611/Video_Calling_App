package com.example.videocallapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.videocallapp.repository.CallLogsRepository

// As our view model has parameter in it's primary constructor
class CallLogsViewModelFactory (private val callLogsRepository: CallLogsRepository) : ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CallLogsFragmentViewModel(callLogsRepository) as T
    }
    // we pass Repository instance here and it creates and return View model instance
}


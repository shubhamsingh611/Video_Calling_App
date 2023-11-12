package com.example.videocallapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videocallapp.R
import com.example.videocallapp.adapters.CallLogsAdapter
import com.example.videocallapp.database.CallLogsDatabase
import com.example.videocallapp.databinding.FragmentCallLogsBinding
import com.example.videocallapp.model.CallLogs
import com.example.videocallapp.repository.CallLogsRepository
import com.example.videocallapp.viewmodel.CallLogsFragmentViewModel
import com.example.videocallapp.viewmodel.CallLogsViewModelFactory


class CallLogsFragment : Fragment() {
    private lateinit var binding : FragmentCallLogsBinding
    lateinit var callLogsFragmentViewModel : CallLogsFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_call_logs,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var callLogsList : RecyclerView = binding.rvCallLogs
        var callLogsObject = mutableListOf<CallLogs>()

        val dao = activity?.let { CallLogsDatabase.getDatabase(it.applicationContext).callLogsDao() }
        val repository = dao?.let { CallLogsRepository(it) }

        callLogsFragmentViewModel =
            ViewModelProvider(
                requireActivity(),
                CallLogsViewModelFactory(repository!!)).get(CallLogsFragmentViewModel::class.java)

        callLogsFragmentViewModel.getCallLogs().observe(viewLifecycleOwner, Observer {
            callLogsObject = it
        })
//testing
//
//        callLogsObject.add(CallLogs(1,"asfds","adfasdfasdf asdfsdaf","ewrwqer"))
//        callLogsObject.add(CallLogs(2,"asfds","asdfsdfsdasdfasdf asdfsdaf","sewasf"))
//        callLogsObject.add(CallLogs(3,"assdfsdffds","asdfsdfsddfasdf asdfsdaf","sadfsdaf"))
//        callLogsObject.add(CallLogs(4,"asfddsfs","asdsdfasdsdf asdfsdaf","sdfasfd"))
//        callLogsObject.add(CallLogs(6,"asfds","asdassddasdf asdfsdaf","sdfdsf"))
//        callLogsObject.add(CallLogs(7,"asfds","dfasdf asdfsdaf","sdfsadfa"))

        callLogsList.adapter = CallLogsAdapter(callLogsObject)
        callLogsList.layoutManager = LinearLayoutManager(activity)
    }
}
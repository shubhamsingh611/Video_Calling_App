package com.example.videocallapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.videocallapp.R
import com.example.videocallapp.adapters.CallLogsAdapter
import com.example.videocallapp.database.CallLogsDatabase
import com.example.videocallapp.databinding.FragmentCallLogsBinding
import com.example.videocallapp.repository.CallLogsRepository
import com.example.videocallapp.viewmodel.CallLogsFragmentViewModel
import com.example.videocallapp.viewmodel.CallLogsViewModelFactory


class CallLogsFragment : Fragment() {
    private lateinit var binding: FragmentCallLogsBinding
    private lateinit var callLogsFragmentViewModel: CallLogsFragmentViewModel

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
        //Setting up recyclerview for Call Logs
        var callLogsList: RecyclerView = binding.rvCallLogs
        val dividerItemDecoration = DividerItemDecoration(
            context,
            LinearLayoutManager.VERTICAL
        )
        callLogsList.addItemDecoration(dividerItemDecoration)

        //Fetching Repository Data
        val dao = CallLogsDatabase.getDatabase(requireContext().applicationContext).callLogsDao()
        val repository = CallLogsRepository(dao)

        //Initializing ViewModel
        callLogsFragmentViewModel =
            ViewModelProvider(
                requireActivity(),
                CallLogsViewModelFactory(repository!!)
            ).get(CallLogsFragmentViewModel::class.java)
        callLogsFragmentViewModel.getCallLogs().observe(requireActivity(), Observer {
            //adapter
            callLogsList.adapter = CallLogsAdapter(it)
            callLogsList.layoutManager = LinearLayoutManager(activity)
        })
    }
}

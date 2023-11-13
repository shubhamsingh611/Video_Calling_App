package com.example.videocallapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.videocallapp.R
import com.example.videocallapp.model.CallLogs

class CallLogsAdapter(private val callLogs : List<CallLogs>): RecyclerView.Adapter<CallLogsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.call_logs_view, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txtPhoneNumber.text = callLogs[position].phoneNumber
        holder.txtCallDuration.text = callLogs[position].callDuration
        holder.txtTimeStamp.text = callLogs[position].timeStamp

    }

    override fun getItemCount(): Int {
        return callLogs.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var txtPhoneNumber: TextView = itemView.findViewById<TextView>(R.id.phone_number)
        var txtCallDuration: TextView = itemView.findViewById<TextView>(R.id.call_duration)
        var txtTimeStamp: TextView = itemView.findViewById<TextView>(R.id.time_stamp)
    }
}
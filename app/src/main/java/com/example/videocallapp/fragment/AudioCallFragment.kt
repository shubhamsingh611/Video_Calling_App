package com.example.videocallapp.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.videocallapp.BuildConfig
import com.example.videocallapp.database.CallLogsDatabase
import com.example.videocallapp.databinding.FragmentAudioCallBinding
import com.example.videocallapp.model.CallLogs
import com.example.videocallapp.repository.CallLogsRepository
import com.example.videocallapp.utils.AppConstants
import com.example.videocallapp.viewmodel.CallLogsFragmentViewModel
import com.example.videocallapp.viewmodel.CallLogsViewModelFactory
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import java.util.Date
import kotlin.random.Random


class AudioCallFragment : Fragment() {

    private lateinit var binding: FragmentAudioCallBinding
    private lateinit var callLogsFragmentViewModel: CallLogsFragmentViewModel
    private val uid = AppConstants.USER_ID
    private var isJoined = false
    private var agoraEngine: RtcEngine? = null
    private val PERMISSION_REQ_ID = AppConstants.PERMISSION_REQ_ID;

    private val REQUESTED_PERMISSIONS = arrayOf(
        android.Manifest.permission.RECORD_AUDIO
    )

    //Checking for Permissions
    private fun checkSelfPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireActivity(),
                REQUESTED_PERMISSIONS[0]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            false
        } else true
    }

    fun showMessage(message: String?) {
        activity?.runOnUiThread {
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupVoiceSDKEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = activity?.baseContext
            config.mAppId = BuildConfig.APP_ID
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            agoraEngine!!.enableAudio()
        } catch (e: Exception) {
            throw RuntimeException("Check the error.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            com.example.videocallapp.R.layout.fragment_audio_call,
            container,
            false
        )
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        agoraEngine!!.stopPreview()
        agoraEngine!!.leaveChannel()

        Thread {
            RtcEngine.destroy()
            agoraEngine = null
        }.start()
    }

    private fun joinChannel() {
        val options = ChannelMediaOptions()
        options.autoSubscribeAudio = true
        // Set both clients as the BROADCASTER.
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        // Set the channel profile as BROADCASTING.
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
        // Join the channel with a temp token.
        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
        agoraEngine!!.startPreview()
        agoraEngine!!.joinChannel(BuildConfig.TOKEN, BuildConfig.CHANNEL_NAME, uid, options)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Accessing Repository for adding call logs to database
        val dao = CallLogsDatabase.getDatabase(requireContext().applicationContext).callLogsDao()
        val repository = CallLogsRepository(dao)

        callLogsFragmentViewModel =
            ViewModelProvider(
                requireActivity(),
                CallLogsViewModelFactory(repository!!)
            ).get(CallLogsFragmentViewModel::class.java)

        binding.joinButton.setOnClickListener {
            if (!isJoined) {
                timer.start()
                binding.infoText.text = AppConstants.CALL_PROGRESS_TEXT
                Toast.makeText(activity, AppConstants.JOINED_CHANNEL, Toast.LENGTH_SHORT).show()
                joinChannel()
            } else {
                Toast.makeText(activity, AppConstants.ALREADY_JOINED, Toast.LENGTH_SHORT).show()
            }
        }


        binding.LeaveButton.setOnClickListener {
            binding.infoText.text = AppConstants.START_AUDIO_CALL
                if (isJoined) {
                    timer.cancel()
                    addUserRecordToLogs()
                    agoraEngine!!.leaveChannel()
                } else {
                    Toast.makeText(activity, AppConstants.JOINED_CHANNEL_FIRST, Toast.LENGTH_SHORT)
                        .show()
                }
        }

        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(REQUESTED_PERMISSIONS[0]), PERMISSION_REQ_ID
            )
        }
        setupVoiceSDKEngine();
    }

    //Timer Implementation
    var timerCounter = 0
    val timer = object : CountDownTimer(200000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            timerCounter++
        }

        override fun onFinish() {}
    }

    private fun addUserRecordToLogs() {
        val preferences = this.requireActivity()
            .getSharedPreferences(AppConstants.SHARED_PREF_TAG, Context.MODE_PRIVATE)
        val mobile = preferences.getString(AppConstants.USER_MOBILE_TEXT, null)
        val sdf = SimpleDateFormat("'Date :'dd-MM-yyyy',\nTime :'HH:mm:ss z")
        val currentDateAndTime = sdf.format(Date())
        val r = Random(System.currentTimeMillis())
        val id = 10000 + r.nextInt(20000)
        val callDuration = "${timerCounter / 60} min ${timerCounter % 60} sec"

        //creating user object and adding to Database
        val callLogs = CallLogs(id, mobile.toString(), currentDateAndTime, callDuration)
        callLogsFragmentViewModel.insertCallLogs(callLogs)
    }

    //Handing Response
    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote user joining the channel.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            activity?.runOnUiThread {
                Toast.makeText(
                    activity,
                    "Remote user joined: $uid",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            // Successfully joined a channel
            isJoined = true
            showMessage("Joined Channel $channel")
            Toast.makeText(activity, AppConstants.JOINED_CHANNEL, Toast.LENGTH_SHORT).show()
            activity?.runOnUiThread { }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            // Listen for remote users leaving the channel
            showMessage("Remote user offline $uid $reason")
            Toast.makeText(activity, AppConstants.OFFLINE_MSG, Toast.LENGTH_SHORT).show()
            if (isJoined) activity?.runOnUiThread { }
        }

        override fun onLeaveChannel(stats: RtcStats) {
            // Listen for the local user leaving the channel
            Toast.makeText(activity, AppConstants.CHANNEL_LEFT, Toast.LENGTH_SHORT).show()
            activity?.runOnUiThread { }
            isJoined = false
        }
    }
}
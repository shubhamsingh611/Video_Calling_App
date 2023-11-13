package com.example.videocallapp.fragment

import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.videocallapp.R
import com.example.videocallapp.database.CallLogsDatabase
import com.example.videocallapp.databinding.FragmentVideoCallBinding
import com.example.videocallapp.model.CallLogs
import com.example.videocallapp.repository.CallLogsRepository
import com.example.videocallapp.viewmodel.CallLogsFragmentViewModel
import com.example.videocallapp.viewmodel.CallLogsViewModelFactory
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Date
import java.util.TreeSet
import kotlin.random.Random


class VideoCallFragment : Fragment() {
    /*
    * TO-DO
    * Define appID and appCertificate in build config
    *
    * */
    private lateinit var binding: FragmentVideoCallBinding
    private val appId = "cec8d97106e04253a7c13a96355d0516"
    var appCertificate = ""
    var expirationTimeInSeconds = 3600

    private val channelName = "shubhamdevelopsapp"
    private val token = "007eJxTYAg76W6hHbKTY/XZvg8f/2gKPK/eJ+42eb6EyMfaTecM1EUVGJJTky1SLM0NDcxSDUyMTI0TzZMNjRMtzYxNTVMMTA3N2twCUxsCGRl+dhYwMEIhiC/EUJxRmpSRmJuSWpaak19QnFhQwMAAAPxsJFY="
    private val uid = 0
    private var isJoined = false

    private var agoraEngine: RtcEngine? = null

    private var localSurfaceView: SurfaceView? = null

    private var remoteSurfaceView: SurfaceView? = null

    private val PERMISSION_REQ_ID = 2023;
    private val REQUESTED_PERMISSIONS =
        arrayOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA
        )

    private lateinit var callLogsFragmentViewModel : CallLogsFragmentViewModel


    private fun checkSelfPermission(): Boolean {
        return !(ContextCompat.checkSelfPermission(
            requireActivity(),
            REQUESTED_PERMISSIONS[0]
        ) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    REQUESTED_PERMISSIONS[1]
                ) != PackageManager.PERMISSION_GRANTED)
    }

    private fun showMessage(message: String) {
        activity?.runOnUiThread {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupVideoSDKEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = activity?.baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            // By default, the video module is disabled, call enableVideo to enable it.
            agoraEngine!!.enableVideo()
        } catch (e: Exception) {
            showMessage(e.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_video_call,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = CallLogsDatabase.getDatabase(requireContext().applicationContext).callLogsDao()
        val repository = CallLogsRepository(dao)

        callLogsFragmentViewModel =
            ViewModelProvider(
                requireActivity(),
                CallLogsViewModelFactory(repository!!)
            ).get(CallLogsFragmentViewModel::class.java)


        binding.JoinButton.setOnClickListener {
            binding.localVideoViewContainer.visibility = View.VISIBLE
            binding.localVideoViewContainer.bringToFront()
            binding.remoteVideoViewContainer.visibility = View.VISIBLE
            joinCall()

        }

        binding.LeaveButton.setOnClickListener {
            binding.localVideoViewContainer.visibility = View.GONE
            binding.remoteVideoViewContainer.visibility = View.GONE
            timer.cancel()
            leaveCall()
            addUserRecordToLogs()
        }

        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUESTED_PERMISSIONS,
                PERMISSION_REQ_ID
            );
        }
        setupVideoSDKEngine();

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

    private fun leaveCall() {
        if (!isJoined) {
            showMessage("Join a channel first")
        } else {
            agoraEngine!!.leaveChannel()
            showMessage("You left the channel")
            if (remoteSurfaceView != null) remoteSurfaceView!!.visibility = View.GONE
            if (localSurfaceView != null) localSurfaceView!!.visibility = View.GONE
            isJoined = false
        }
    }

    private fun joinCall() {
        if (checkSelfPermission()) {
            timer.start()
            val options = ChannelMediaOptions()
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            setupLocalVideo()
            localSurfaceView!!.visibility = View.VISIBLE
            agoraEngine!!.startPreview()
            agoraEngine!!.joinChannel(token, channelName, uid, options)
        } else {
            Toast.makeText(activity, "Permissions was not granted", Toast.LENGTH_SHORT)
                .show()
        }
    }
    var timerCounter = 0

    val timer = object: CountDownTimer(200000, 1000) {
        override fun onTick(millisUntilFinished: Long) {timerCounter++}
        override fun onFinish() {}
    }

    private fun addUserRecordToLogs() {
        val preferences = this.requireActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        val mobile = preferences.getString("user_mobile",null)
        val sdf = SimpleDateFormat("'Date :'dd-MM-yyyy',\nTime :'HH:mm:ss z")
        val currentDateAndTime = sdf.format(Date())
        val r = Random(System.currentTimeMillis())
        val id = 10000 + r.nextInt(20000)
        Log.e("Results Time ", "$id $mobile $currentDateAndTime ${timerCounter%60} ${timerCounter/60} ")
        val callDuration = "${timerCounter/60} min ${timerCounter%60} sec"
        val callLogs = CallLogs(id,mobile.toString(), currentDateAndTime,callDuration)
        callLogsFragmentViewModel.insertCallLogs(callLogs)
    }

    private val mRtcEventHandler: IRtcEngineEventHandler =
        object : IRtcEngineEventHandler() {

            override fun onUserJoined(uid: Int, elapsed: Int) {
                showMessage("Remote user joined $uid")
                // Set the remote video view
                activity?.runOnUiThread { setupRemoteVideo(uid) }
            }

            override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
                isJoined = true
                showMessage("Joined Channel $channel")

            }

            override fun onUserOffline(uid: Int, reason: Int) {
                showMessage("Remote user offline $uid $reason")
                activity?.runOnUiThread { remoteSurfaceView!!.visibility = View.GONE }
            }

        }

    private fun setupRemoteVideo(uid: Int) {
        remoteSurfaceView = SurfaceView(activity?.baseContext)
        remoteSurfaceView!!.setZOrderMediaOverlay(true)
        binding.remoteVideoViewContainer.addView(remoteSurfaceView)
        agoraEngine!!.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
        remoteSurfaceView!!.visibility = View.VISIBLE
    }

    private fun setupLocalVideo() {
        localSurfaceView = SurfaceView(activity?.baseContext)
        binding.localVideoViewContainer.addView(localSurfaceView)
        agoraEngine!!.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                0
            )
        )
    }

}
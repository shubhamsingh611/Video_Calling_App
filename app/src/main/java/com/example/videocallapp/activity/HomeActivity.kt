package com.example.videocallapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.videocallapp.R
import com.example.videocallapp.databinding.ActivityHomeBinding
import com.example.videocallapp.fragment.AudioCallFragment
import com.example.videocallapp.fragment.CallLogsFragment
import com.example.videocallapp.fragment.VideoCallFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(VideoCallFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.video_call -> replaceFragment(VideoCallFragment())
                R.id.audio_call -> replaceFragment(AudioCallFragment())
                R.id.recent -> replaceFragment(CallLogsFragment())
                else ->{
                }
            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}
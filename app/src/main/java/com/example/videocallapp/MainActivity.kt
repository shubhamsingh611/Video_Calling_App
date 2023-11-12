package com.example.videocallapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.videocallapp.databinding.ActivityMainBinding
import com.example.videocallapp.fragment.AudioCallFragment
import com.example.videocallapp.fragment.CallLogsFragment
import com.example.videocallapp.fragment.LoginFragment
import com.example.videocallapp.fragment.LoginVerificationFragment
import com.example.videocallapp.fragment.VideoCallFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Inside your activity
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

// Replace 'YourFragment' with the actual name of your fragment class
        val fragment = LoginFragment()

// If you have arguments to pass to the fragment, you can set them here
// val bundle = Bundle()
// bundle.putString("key", "value")
// fragment.arguments = bundle

        fragmentTransaction.replace(R.id.container_main_activity, fragment)
        fragmentTransaction.addToBackStack(null) // Optional, if you want to add this transaction to the back stack
        fragmentTransaction.commit()

        //Checking Internet connection
        if (NetworkUtils.isInternetAvailable(this)) {
            //Api Calling
        } else {
            //Showing Alert Dialog for no network
            val alertDialog = AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Network Error!")
                .setMessage("Internet is not available. Turn on Internet and Try again.")
                .show()
        }


    }


}
package com.example.videocallapp

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.videocallapp.databinding.ActivityMainBinding
import com.example.videocallapp.fragment.LoginFragment
import com.example.videocallapp.utils.AppConstants

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val loginFragment = LoginFragment()

        fragmentTransaction.replace(R.id.container_main_activity, loginFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        //Checking Internet connection
        if (NetworkUtils.isInternetAvailable(this)) {
            //Api Calling
        } else {
            //Showing Alert Dialog for no network
            val alertDialog = AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(AppConstants.NETWORK_ERROR)
                .setMessage(AppConstants.NETWORK_ERROR_MSG)
                .show()
        }
    }
}
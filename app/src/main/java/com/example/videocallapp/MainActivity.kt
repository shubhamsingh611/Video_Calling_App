package com.example.videocallapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.videocallapp.databinding.ActivityMainBinding
import com.example.videocallapp.fragment.LoginFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = LoginFragment()

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
package com.example.videocallapp.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.videocallapp.R
import com.example.videocallapp.databinding.FragmentLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_login, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonGetOtp.setOnClickListener {
            if (binding.editTextPhone.text.toString().trim().isNotEmpty()) {
                if (binding.editTextPhone.text.toString().trim().length == 10) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.buttonGetOtp.visibility = View.INVISIBLE

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + binding.editTextPhone.text.toString(),
                        60,
                        TimeUnit.SECONDS, this.requireActivity(),
                        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                                binding.progressBar.visibility = View.GONE
                                binding.buttonGetOtp.visibility = View.VISIBLE
                            }

                            override fun onVerificationFailed(p0: FirebaseException) {
                                binding.progressBar.visibility = View.GONE
                                binding.buttonGetOtp.visibility = View.VISIBLE
                                Toast.makeText(activity, p0.message, Toast.LENGTH_SHORT).show()
                            }

                            override fun onCodeSent(
                                backEndOtp: String,
                                p1: PhoneAuthProvider.ForceResendingToken
                            ) {
                                binding.progressBar.visibility = View.GONE
                                binding.buttonGetOtp.visibility = View.VISIBLE

                                val loginVerificationFragment = LoginVerificationFragment()
                                val bundle = Bundle()
                                Log.d(
                                    "Back End OTP 1 ",
                                    backEndOtp + "new hai yaar - " + binding.editTextPhone.text.toString()
                                )

                                val preferences = requireActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
                                val editor = preferences.edit()
                                editor.putString("user_mobile","+91-" + binding.editTextPhone.text.toString()).apply()

                                bundle.putString("mobile", binding.editTextPhone.text.toString())
                                bundle.putString("backendotp", backEndOtp)

                                loginVerificationFragment.arguments = bundle
                                requireActivity()!!.supportFragmentManager.beginTransaction()
                                    .replace(
                                        R.id.container_main_activity,
                                        loginVerificationFragment,
                                        "findThisFragment"
                                    )
                                    .addToBackStack(null)
                                    .commit()
                            }

                        }
                    )

                } else {
                    Toast.makeText(activity, "Please enter correct number!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(activity, "Enter Mobile number!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}